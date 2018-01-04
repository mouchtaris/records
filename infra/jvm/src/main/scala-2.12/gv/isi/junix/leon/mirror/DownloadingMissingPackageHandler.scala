package gv
package isi
package junix
package leon
package mirror

import
  scala.concurrent.{
    Future,
    ExecutionContext,
  },
  scala.collection.concurrent.{
    TrieMap,
  },
  akka.{
    NotUsed,
  },
  akka.http.scaladsl.model.{
    Uri,
  },
  akka.stream.{
    ActorMaterializer,
    ActorAttributes,
    Supervision,
    FlowShape,
    OverflowStrategy,
  },
  akka.stream.scaladsl.{
    Source,
    Flow,
    Keep,
    GraphDSL,
    Broadcast,
    Zip,
  },
  akka.util.{
    ByteString,
  },
  akka.event.{
    LoggingAdapter,
  },
  fun.{
    Named,
  },
  akkadecos.{
    FlowBalanceDecoration,
    FlowThroughDecoration,
  }

object DownloadingMissingPackageHandler {

  object RequestId extends Named[String]

  final case class Context(
    requestId: RequestId.T,
    uri: Uri,
  )

  object Context {
    val empty = Context(RequestId(""), Uri())
  }

  def repeating[T](t: Traversable[T]): Stream[T] = {
    val stream = t.toStream
    t.toStream #::: repeating(stream)
  }

}

trait DownloadingMissingPackageHandler
  extends AnyRef
  with MissingPackageHandler
{
  protected[this] val downloadManager: DownloadManager[DownloadingMissingPackageHandler.Context]
  protected[this] implicit val materializer: ActorMaterializer
  protected[this] val log: LoggingAdapter
  protected[this] val state: State

  final type Context = DownloadingMissingPackageHandler.Context

  import
    DownloadingMissingPackageHandler.{
      Context,
      RequestId,
      repeating,
    },
    downloadManager.{
      download,
    },
    DownloadManager.{
      downloadDispatcherName,
      downloadDispatcherAttribute,
    }

  private[this] object privates {

    val mirrorIterators: TrieMap[leon.Mirror.Name.T, Iterator[Uri]] = TrieMap.empty
    val resumingStrategy = ActorAttributes supervisionStrategy Supervision.resumingDecider
    lazy val downloadDispatcher = materializer.system.dispatchers.lookup(downloadDispatcherName)

    object func {
      val getMirror = (_: Input)._1
      val getPath = (_: Input)._2
      val getSink = (_: Input)._3

      val makeRequestId: Input ⇒ RequestId.T = input ⇒ RequestId {
        getMirror(input).name + ":" + getPath(input)
      }

      val makeContext = (input: Input) ⇒ Context(
        makeRequestId(input),
        makeUri(input)
      )

      val getIterator: Input ⇒ Iterator[Uri] = input ⇒ {
        val mirror = getMirror(input)
        import mirror.{ name, baseUris }
        def newIterator = repeating(baseUris).toIterator
        mirrorIterators getOrElseUpdate (name, newIterator)
      }

      val getBase: Input ⇒ Uri =
        getIterator andThen (_.next())

      val makeUri: Input ⇒ Uri = input ⇒ {
        val base = getBase(input)
        val path = getPath(input)
        val fullPath = base.path ++ path
        base withPath fullPath
      }

      val inputToRequestInput: Input ⇒ (Uri, Context) =
        input ⇒ {
          val uri = makeUri(input)
          val context = makeContext(input)
          val requestId = context.requestId
          state.downloading(requestId)
          (uri, context)
        }

      val dlResultToSource: PartialFunction[downloadManager.Result, Source[ByteString, Context]] = {
        case downloadManager.Results.Downloading(context, source) ⇒
          import context.requestId
          log debug s"writing to source ($requestId)"
          source mapMaterializedValue (_ ⇒ context)
        case downloadManager.Results.HttpError(context, resp) ⇒
          import context.uri
          resp.entity.discardBytes()
          log debug s"http error $uri $resp"
          Source failed new Exception(s"HTTP error $context : $resp") mapMaterializedValue (_ ⇒ context)
        case downloadManager.Results.OtherFailure(context, ex) ⇒
          import context._
          log debug s"other failure ($requestId, $uri): $ex"
          Source failed ex mapMaterializedValue (_ ⇒ context)
      }

      val sipSource: ((Input, Source[ByteString, Context])) ⇒ Future[Context] = {
        case (input, source) ⇒
          implicit val ec: ExecutionContext = downloadDispatcher
          log debug s"sipping begins: ${(getMirror(input), getPath(input))}"
          val (context, future) = source
//            .map { bs ⇒ log debug s"sipping ${bs.length}"; bs }
            .toMat(getSink(input))(Keep.both)
            .withAttributes(downloadDispatcherAttribute)
            .run()
          import context.requestId
          state.sipping(requestId)
          def markDone(): Unit = {
            log debug s"sinking done ${context.uri}"
            state done requestId
          }
          future
            .map { _ ⇒ markDone(); context }
            .recoverWith { case ex ⇒ markDone(); Future failed ex }
        }
    }

    object flows {

      lazy val inputToDownload =
        Flow
          .fromFunction(func.inputToRequestInput)
          .via(download)
          .via(Flow fromFunction func.dlResultToSource)
          .buffer(100, OverflowStrategy.backpressure)

      lazy val sipSource =
        Flow
          .fromFunction(func.sipSource)
          .mapAsyncUnordered(1)(identity)
          .withAttributes(downloadDispatcherAttribute)
          .withAttributes(resumingStrategy)
          .balance(4)
          .map { r ⇒ log debug s"sipping moving on $r" ; r }

      lazy val processGraph = GraphDSL.create() { implicit b ⇒
        import GraphDSL.Implicits._
        val bcastInput = b add Broadcast[Input](2)
        val zip = b add Zip[Input, Source[ByteString, Context]]()
        val sip = b add sipSource

        bcastInput ~>                     zip.in0
        bcastInput ~> inputToDownload ~>  zip.in1

        zip.out ~> Flow[(Input, Source[ByteString, Context])]
          .map{r ⇒ log debug s"dlmanager zip out $r" ; r}
          .buffer(100, OverflowStrategy.backpressure) ~> sip

        FlowShape(bcastInput.in, sip.out)
      }

      lazy val process = Flow[Input]
        .buffer(10000, OverflowStrategy.backpressure)
        .via(Flow.fromGraph(processGraph))
    }

  }

  final lazy val flow: Flow[Input, Context, NotUsed] = {
    log debug " +++++++++++ MISSING PROCESS REQUESTED"
    privates.flows.process
  }

}
