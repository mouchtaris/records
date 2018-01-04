package gv

import
  lang.{
    ImplicitlyAvailable,
    Now,
  },
  string._

package object akkadecos {
  type  Try[T]                      = scala.util.Try[T]
  val   Try                         = scala.util.Try
  type  Success[T]                  = scala.util.Success[T]
  val   Success                     = scala.util.Success
  type  Failure[T]                  = scala.util.Failure[T]
  val   Failure                     = scala.util.Failure

  type  Future[T]                   = scala.concurrent.Future[T]
  val   Future                      = scala.concurrent.Future
  type  ExecutionContext            = scala.concurrent.ExecutionContext
  val   ExecutionContext            = scala.concurrent.ExecutionContext

  type  NotUsed                     = akka.NotUsed
  val   NotUsed                     = akka.NotUsed
  type  Done                        = akka.Done
  val   Done                        = akka.Done

  type  Actor                       = akka.actor.Actor
  val   Actor                       = akka.actor.Actor
  type  ActorRef                    = akka.actor.ActorRef
  val   ActorRef                    = akka.actor.ActorRef
  type  ActorSystem                 = akka.actor.ActorSystem
  val   ActorSystem                 = akka.actor.ActorSystem
  type  Inbox                       = akka.actor.Inbox
  val   Inbox                       = akka.actor.Inbox
  type  Props                       = akka.actor.Props
  val   Props                       = akka.actor.Props


  type  Attributes                  = akka.stream.Attributes
  val   Attributes                  = akka.stream.Attributes
  val   ActorAttributes             = akka.stream.ActorAttributes
  type  SourceShape[+T]             = akka.stream.SourceShape[T]
  val   SourceShape                 = akka.stream.SourceShape
  type  FlowShape[-I, +O]           = akka.stream.FlowShape[I, O]
  val   FlowShape                   = akka.stream.FlowShape
  type  SinkShape[-T]               = akka.stream.SinkShape[T]
  val   SinkShape                   = akka.stream.SinkShape
  type  UniformFanInShape[-T, +O]   = akka.stream.UniformFanInShape[T, O]
  val   UniformFanInShape           = akka.stream.UniformFanInShape
  type  UniformFanOutShape[-I, +O]  = akka.stream.UniformFanOutShape[I, O]
  type  IOResult                    = akka.stream.IOResult
  val   IOResult                    = akka.stream.IOResult
  type  Materializer                = akka.stream.Materializer
  type  ActorMaterializer           = akka.stream.ActorMaterializer
  val   ActorMaterializer           = akka.stream.ActorMaterializer
  type  Shape                       = akka.stream.Shape
  type  Graph[+S <: Shape, +M]      = akka.stream.Graph[S, M]
  type  Inlet[T]                    = akka.stream.Inlet[T]
  val   Inlet                       = akka.stream.Inlet
  type  Outlet[T]                   = akka.stream.Outlet[T]
  val   Outlet                      = akka.stream.Outlet

  type GraphStage[S <: Shape]       = akka.stream.stage.GraphStage[S]
  type GraphStageLogic              = akka.stream.stage.GraphStageLogic
  val  GraphStageLogic              = akka.stream.stage.GraphStageLogic
  type OutHandler                   = akka.stream.stage.OutHandler
  type InHandler                    = akka.stream.stage.InHandler

  val   Keep                        = akka.stream.scaladsl.Keep
  type  Source[+Out, +Mat]          = akka.stream.scaladsl.Source[Out, Mat]
  val   Source                      = akka.stream.scaladsl.Source
  type  Flow[-In, +Out, +Mat]       = akka.stream.scaladsl.Flow[In, Out, Mat]
  val   Flow                        = akka.stream.scaladsl.Flow
  type  Sink[-In, +Mat]             = akka.stream.scaladsl.Sink[In, Mat]
  val   Sink                        = akka.stream.scaladsl.Sink
  val   Broadcast                   = akka.stream.scaladsl.Broadcast
  val   Merge                       = akka.stream.scaladsl.Merge
  val   Zip                         = akka.stream.scaladsl.Zip
  val   Balance                     = akka.stream.scaladsl.Balance
  val   GraphDSL                    = akka.stream.scaladsl.GraphDSL
  type  SinkQueue[T]                = akka.stream.scaladsl.SinkQueue[T]
  type  SinkQueueWithCancel[T]      = akka.stream.scaladsl.SinkQueueWithCancel[T]
  type  SourceQueue[T]              = akka.stream.scaladsl.SourceQueue[T]
  type  SourceQueueWithComplete[T]  = akka.stream.scaladsl.SourceQueueWithComplete[T]

  type  HttpExt                     = akka.http.scaladsl.HttpExt
  val   Http                        = akka.http.scaladsl.Http

  val   RequestBuilding             = akka.http.scaladsl.client.RequestBuilding
  type  RequestBuilding             = akka.http.scaladsl.client.RequestBuilding

  type  HttpRequest                 = akka.http.scaladsl.model.HttpRequest
  val   HttpRequest                 = akka.http.scaladsl.model.HttpRequest
  type  HttpHeader                  = akka.http.scaladsl.model.HttpHeader
  val   HttpHeader                  = akka.http.scaladsl.model.HttpHeader
  type  HttpResponse                = akka.http.scaladsl.model.HttpResponse
  val   HttpResponse                = akka.http.scaladsl.model.HttpResponse
  type  Uri                         = akka.http.scaladsl.model.Uri
  val   Uri                         = akka.http.scaladsl.model.Uri
  type  StatusCode                  = akka.http.scaladsl.model.StatusCode
  val   StatusCode                  = akka.http.scaladsl.model.StatusCode
  val   StatusCodes                 = akka.http.scaladsl.model.StatusCodes
  type  ResponseEntity              = akka.http.scaladsl.model.ResponseEntity
  val   ResponseEntity              = akka.http.scaladsl.model.ResponseEntity
  type  HttpEntity                  = akka.http.scaladsl.model.HttpEntity
  val   HttpEntity                  = akka.http.scaladsl.model.HttpEntity
  type  ContentType                 = akka.http.scaladsl.model.ContentType
  val   ContentType                 = akka.http.scaladsl.model.ContentType
  val   ContentTypes                = akka.http.scaladsl.model.ContentTypes

  type  Route                       = akka.http.scaladsl.server.Route
  val   Route                       = akka.http.scaladsl.server.Route
  type  Directives                  = akka.http.scaladsl.server.Directives
  val   Directives                  = akka.http.scaladsl.server.Directives

  type  ByteString                  = akka.util.ByteString
  val   ByteString                  = akka.util.ByteString


  final implicit class FlowThroughDecoration[in, out, mat](val self: Flow[in, out, mat]) extends AnyVal {

    def through[out1, out2, mat1, mat2](
      flow1: Flow[out, out1, mat1],
      flow2: Flow[out, out2, mat2],
    ): Flow[in, (out1, out2), (mat, mat1, mat2)] =
      Flow fromGraph GraphDSL.create(self, flow1, flow2)(Tuple3.apply) { implicit b ⇒ (slf, f1, f2) ⇒
        import GraphDSL.Implicits._
        val bcast = b add Broadcast[out](2)
        val zip = b add Zip[out1, out2]

        slf.out ~>  bcast.in
                    bcast.out(0) ~> f1.in; f1.out ~> zip.in0
                    bcast.out(1) ~> f2.in; f2.out ~> zip.in1

        FlowShape(slf.in, zip.out)
      }

  }

  def balancer[In, Out](worker: Flow[In, Out, Any], workerCount: Int): Flow[In, Out, NotUsed] = {
    import GraphDSL.Implicits._

    Flow.fromGraph(GraphDSL.create() { implicit b ⇒
      val balancer = b.add(Balance[In](workerCount, waitForAllDownstreams = false))
      val merge = b.add(Merge[Out](workerCount))

      for (_ ← 1 to workerCount) {
        // for each worker, add an edge from the balancer to the worker, then wire
        // it to the merge element
        balancer ~> worker.async ~> merge
      }

      FlowShape(balancer.in, merge.out)
    })
  }

  final implicit class FlowBalanceDecoration[in, out, mat](val self: Flow[in, out, mat]) extends AnyVal {
    def balance(workers: Int): Flow[in, out, NotUsed] =
      balancer(self, workers)
  }

  final implicit case object HttpRequestInspect extends Inspect[HttpRequest] {
    def apply(req: HttpRequest): Stream[(String, Any)] =
      ("uri" → req.uri) #::
      ("headers" → req.headers.mkString(", ")) #::
      ("cookies" → req.cookies.mkString(", ")) #::
      ("entity" → req.entity) #::
      Stream.empty
  }

  object HttpRequestPrettyInspect {
    final val ListPrefix: String = "\n      - "
  }
  final implicit class HttpRequestPrettyInspect(val self: HttpRequest) extends AnyVal {
    import self._
    import HttpRequestPrettyInspect._

    private[this] def makeList[T](seq: Traversable[T]): String =
      seq mkString ListPrefix

    private[this] def headersList: String = makeList(headers)
    private[this] def cookiesList: String = makeList(cookies)

    def prettyInspect: String =
      s"""
         |    --- URI:
         |      $uri
         |    --- Headers:
         |      - $headersList
         |    --- Cookies:
         |      - $cookiesList
         |    --- Entity:
         |      $entity
       """.stripMargin
  }

  trait TypedActorCompanion extends ImplicitlyAvailable {
    type Message <: Any
    final type TypedReceive = scala.PartialFunction[Message, Unit]
  }

  abstract class TypedActor[Companion <: TypedActorCompanion](
    implicit
    val companion: Companion
  )
    extends Actor
  {
    final type TypedReceive = companion.TypedReceive
    final type Message = companion.Message

    def typedReceive: TypedReceive

    final def receive: Receive = {
      case any: Message ⇒
        typedReceive(any)
    }
  }

  object _HttpResponse {

    def unapply(resp: HttpResponse): HttpResponseUnpacked =
      resp

    object Success {
      def unapply(status: StatusCode): Boolean =
        status.isSuccess
    }

    final implicit class HttpResponseUnpacked(val resp: HttpResponse) extends AnyVal {
      def isEmpty: Boolean = false
      def get: (StatusCode, ResponseEntity) = (resp.status, resp.entity)
    }
  }

  trait Identity[T] extends GraphStage[FlowShape[T, T]] {
    val in = Inlet[T]("inlet")
    val out = Outlet[T]("piglet")
    val shape = FlowShape(in, out)

    def createLogic(inheritedAttributes: Attributes): GraphStageLogic = new GraphStageLogic(shape) with InHandler with OutHandler {
      def onPush(): Unit = push(out, grab(in))
      def onPull(): Unit = pull(in)

      setHandler(in, this)
      setHandler(out, this)
    }

    override def toString = "Identity"
  }

  final class Sneako[T](val clue: String)(implicit system: ActorSystem) extends Identity[T] {
    override def createLogic(inheritedAttributes: Attributes): GraphStageLogic = {
      system.log debug s"Materializing sneak $clue"
      super.createLogic(inheritedAttributes)
    }
  }

  final class MaterializedOnce[T](val clue: String) extends Identity[T] {
    private[this] val created = scala.collection.concurrent.TrieMap.empty[Boolean, Boolean]

    override def createLogic(inheritedAttributes: Attributes): GraphStageLogic = {
      if (created.nonEmpty)
        throw new IllegalStateException(s"Rematerializing this graph ($clue) is ILLEGAL")
      created.getOrElseUpdate(true, true)
      super.createLogic(inheritedAttributes)
    }
  }

  object MaterializedOnce {
    def apply[T](clue: String) = Flow fromGraph new MaterializedOnce[T](clue)
  }

  trait SourceFromSinkQueueConstructor[T] extends Any {

    def apply(queue: SinkQueue[T]): Source[T, NotUsed] =
      Source.unfoldAsync(NotUsed) { _ ⇒
        queue.pull()
          .map(_ map ((NotUsed, _)))(Now)
      }
      .via(MaterializedOnce[T]("SourceFromSinkQueue"))

  }

  object SourceMore {

    class FromSinkQueue[T](val self: Unit)
      extends AnyVal
      with SourceFromSinkQueueConstructor[T]

    def apply[T](queue: SinkQueue[T]): Source[T, NotUsed] =
      new FromSinkQueue[T](())(queue)

  }

}
