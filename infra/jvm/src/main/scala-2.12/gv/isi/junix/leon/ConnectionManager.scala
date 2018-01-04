package gv
package isi
package junix
package leon

import
  scala.collection.concurrent.{
    TrieMap,
  },
  scala.concurrent.{
    Future,
    ExecutionContext,
  },
  scala.util.{
    Try,
    Success,
    Failure,
  },
  akka.{
    NotUsed,
  },
  akka.stream.{
    FlowShape,
    Attributes,
    OverflowStrategy,
    Materializer,
    Inlet,
    Outlet,
    QueueOfferResult,
  },
  akka.stream.scaladsl.{
    GraphDSL,
    Source,
    Flow,
    Sink,
    Keep,
    BroadcastHub,
    MergeHub,
    Partition,
    Merge,
    SinkQueueWithCancel,
    SourceQueueWithComplete,
  },
  akka.http.scaladsl.{
    Http,
    HttpExt,
  },
  akka.http.scaladsl.model.{
    Uri,
    HttpRequest,
    HttpResponse,
  },
  lang._,
  fun.{
    Named,
  },
  akkadecos.{
    Sneako,
    SourceMore,
  }

object ConnectionManager
  extends AnyRef
  with LocalException
{
  object Host extends Named[String] {
    val localhost = Host("localhost")
  }
  object Port extends Named[Int]
  object Scheme extends Named[String] {
    val http = apply("http")
    val https = apply("https")
  }

  final case class Key(
    scheme: Scheme.T,
    host: Host.T,
    port: Port.T,
  )

  val getHost: Uri ⇒ Host.T =
    uri ⇒
      Host(uri.authority.host.address)

  val getPort: Uri ⇒ Port.T =
    uri ⇒
      Port(uri.effectivePort)

  val getScheme: Uri ⇒ Scheme.T =
    uri ⇒
      Scheme (uri.scheme)

  def getKey(uri: Uri): Key =
    Key(getScheme(uri), getHost(uri), getPort(uri))
  def getKey(req: HttpRequest): Key =
    getKey(req.uri)

  def getRequest(input: SpecificTypes#Input): HttpRequest =
    input _1

  trait SpecificTypes extends AnyRef {
    type Context
    final type Input = (HttpRequest, Context)
    final type TryInput = (Try[HttpRequest], Context)
    final type Result = (Try[HttpResponse], Context)
    final type Connection = Flow[Input, Result, Any]
    final val Connection = Flow[Input]
    final type NeutralConnection = Flow[Input, Input, NotUsed]
  }

  val InputSourceQueueBuffer = 1000

  sealed trait RequestMatcher extends Any {
    def unapply(request: HttpRequest): Boolean
  }
  object RequestMatcher {
    def apply(key: Key) = new RequestMatcher {
      def unapply(request: HttpRequest): Boolean =
        getKey(request.uri) == key
    }
  }
}

final case class ConnectionManager[context]()(
  implicit
  http: HttpExt,
  materializer: Materializer
)
  extends AnyRef
  with ConnectionManager.SpecificTypes
{
  import ConnectionManager._
  type Context = context

  private[this] implicit val actorSystem = http.system
  private[this] val log = actorSystem.log
  private[this] val connections: TrieMap[Key, Connection] =
    TrieMap.empty

  def newHttpConnection(key: Key): Connection =
    http.cachedHostConnectionPool(host = key.host, port = key.port)

  def newHttpsConnection(key: Key): Connection =
    http.cachedHostConnectionPoolHttps(host = key.host, port = key.port)

  def newSneaky(key: Key): Flow[Input, Input, NotUsed] = {
    log debug s"Actually creating a new SNEAKY $key (${key ##})"
    newSneaky(key.toString)
  }

  def newSneaky[T](clue: String): Flow[T, T, NotUsed] = {
    log debug s"Actually creating a new SNEAKY $clue"
    Flow fromGraph new Sneako[T](clue)
  }

  val createConnection: Key ⇒ Try[Connection] = {
    case key @ Key(Scheme.http, _, _) ⇒
      Success(newSneaky(key).viaMat(newHttpConnection(key))(Keep.right))
    case key @ Key(Scheme.https, _, _) ⇒
      Success(newSneaky(key).viaMat(newHttpsConnection(key))(Keep.right))
    case key ⇒
      Failure(new ConnectionManager.Exception(s"Unknown key: $key"))
  }

  val (
    toAllConnectionsQueue: SourceQueueWithComplete[Input],
    bcastToConnections: Source[Input, NotUsed]
  ) =
    Source
      .queue(InputSourceQueueBuffer, OverflowStrategy.backpressure)
      .toMat(BroadcastHub.sink[Input])(Keep.both)
      .run()

  val (
    mergeConnectionResults: Sink[Result, NotUsed],
    connectionResultsQueue: SinkQueueWithCancel[Result]
  ) =
    MergeHub.source[Result]
      .toMat(Sink.queue())(Keep.both)
      .run()

  val connectionFilter: Key ⇒ Input ⇒ Boolean =
    key ⇒ input ⇒ {
      val result = RequestMatcher(key) unapply input._1
      log debug s"filtering $input for $key : $result"
      result
    }

  val connectionFilterFlow: Key ⇒ Flow[Input, Input, NotUsed] =
    Flow[Input] filter connectionFilter(_)

  def hookUpConnection(key: Key)(connection: Connection): NotUsed = {
    def loclog(msg: String) = log debug s"connection:$key $msg"
    bcastToConnections
      .map { r ⇒ loclog(s"received broadcast request, sending to filter: $r"); r }
      .via(connectionFilterFlow(key))
      .map { r ⇒ loclog(s"filtered to handle, sending to handler: $r"); r }
      .via(connection)
      .map { r ⇒ loclog(s"received connection result, sending to result merge: $r"); r }
      .to(mergeConnectionResults)
      .run()
  }

  def ensureConnection(key: Key): Try[NotUsed] =
    connections.get(key) match {
      case Some(_) ⇒ Success(NotUsed)
      case None ⇒ createConnection(key)
          .map(connections getOrElseUpdate (key, _))
          .map(hookUpConnection(key))
      }

  val tryEnsureConnection: Input ⇒ TryInput = {
    case (request, context) ⇒
      val tryRequest = ensureConnection(getKey(request)) map (_ ⇒ request)
      (tryRequest, context)
  }
  val tryEnsureConnectionFlow: Flow[Input, TryInput, NotUsed] =
    Flow fromFunction tryEnsureConnection

  val sendToInputFlow: NeutralConnection = {
    Connection
      .map { input ⇒
        import materializer.executionContext
        toAllConnectionsQueue
          .offer(input)
          .map { result ⇒ (input, result) }
          .recover { case ex ⇒ (input, QueueOfferResult.Failure(ex)) }
      }
      .mapAsyncUnordered(InputSourceQueueBuffer)(identity)
      .map {
        case (input, QueueOfferResult.Enqueued) ⇒
          input
        case (_, QueueOfferResult.Failure(ex)) ⇒
          throw ex
        case (_, other) ⇒
          throw new Exception(other.toString)
      }
  }

  val connectionsResultsSource: Source[Result, NotUsed] =
    SourceMore(connectionResultsQueue)

  val failedConnection: PartialFunction[TryInput, Result] = {
    case (Failure(ex), context) ⇒
      (Failure(ex), context)
  }
  val failedConnectionFlow: Flow[TryInput, Result, NotUsed] =
    Flow fromFunction failedConnection

  val getSuccessfulConnectionRequest: PartialFunction[TryInput, Input] = {
    case (Success(req), context) ⇒
      (req, context)
  }
  val successfulConnectionSink: Sink[TryInput, NotUsed] =
    Flow[TryInput]
      .map(getSuccessfulConnectionRequest)
      .via(sendToInputFlow)
      .to(Sink.ignore)

  val partitionInput: PartialFunction[TryInput, Int] = {
    case (Failure(_), _) ⇒ 0
    case (Success(_), _) ⇒ 1
  }


  import GraphDSL.Implicits._
  val connection: Connection = Flow fromGraph GraphDSL.create() { implicit b ⇒
    val setupConnection = b add tryEnsureConnectionFlow
    val failedConnection = b add failedConnectionFlow
    val successfulConnection = b add successfulConnectionSink
    val partitionRequests = b add Partition[TryInput](2, partitionInput)
    val connectionsResults = b add connectionsResultsSource
    val mergeAll = b add Merge[Result](2)

    val input = b add Connection

    input ~> setupConnection ~> partitionRequests

    partitionRequests ~> failedConnection ~> mergeAll
    partitionRequests ~> successfulConnection

    // ... -> bcast -> all connections -> merge ... //

    connectionsResults ~> mergeAll

    FlowShape(input.in, mergeAll.out)
  }
}

