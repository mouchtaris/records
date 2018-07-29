package lart
package setup

import akka.NotUsed
import akka.actor.ActorSystem
import akka.http.scaladsl.server._
import akka.http.scaladsl.model.Uri.Path
import akka.stream.Materializer
import lart.http.routes.{CompleteFuture, Unhandled}
import akka.http.scaladsl.{Http, HttpExt}
import akka.http.scaladsl.model.{HttpRequest, HttpResponse, StatusCodes}
import akka.http.scaladsl.server.directives.{DebuggingDirectives, LoggingMagnet}
import akka.http.scaladsl.server.Directives._
import akka.stream.scaladsl.{Flow, Sink}
import akka.util.ByteString
import hm.TypeInfo

import scala.concurrent.duration.Duration
import scala.concurrent.{Await, Future, Promise}

case object HttpServer
  extends AnyRef
  with TypeInfo[HttpServer]
{

  def completePrefix: String = "stop"

}

class HttpServer(
  implicit
  config: ConfigContext,
  akka: AkkaContext,
  logs: LoggingContext,
) {
  import HttpServer._
  import akka.{
    actorSystem,
    materializer,
  }

  private[this] val logger: Logging.Logger = logs.factory[HttpServer]
  private[this] val finishPromise: Promise[Unit] = Promise()
  private[this] val finish: Future[Unit] = finishPromise.future

  logger.info("Good morning")

  private[this] def makeCompleteFuture: Route =
    CompleteFuture(
      pathPrefix = completePrefix,
      completionPromise = finishPromise,
      logger = logs.factory("<complete future>")
    )

  private[this] def forwardToPat(http: HttpExt): HttpRequest ⇒ Future[HttpResponse] =
    req ⇒ {
      val vec1f = req.entity.dataBytes.runWith(Sink.collection[ByteString, Vector[ByteString]])
      val vec2f = req.entity.dataBytes.runWith(Sink.collection[ByteString, Vector[ByteString]])
      val vecsf = vec1f.zip(vec2f)
      val (vec1, vec2) = Await.result(vecsf, Duration.Inf)
      logger.info("vec1 = {} vec2 = {}", vec1.length, vec2.length)
      val uri2 = req.uri
        .withHost("159.65.193.153")
        .withPort(8090)
      val req2 = req.withUri(uri2)
      logger.info("forwarding : {}", req2)
      http.singleRequest(req2)
        .andThen { case res ⇒ logger.info("result came in: {}", res) }(Now)
    }

  private[this] def makeUnhandled(http: HttpExt): Route =
    Unhandled(
      handle = forwardToPat(http),
      logger = logs.factory("<unhandled>")
    )

  private[this] def makeRejectionHandler: RejectionHandler =
    RejectionHandler.newBuilder
      .handle {
        case rejection ⇒
          logger.warning("Rejected: {}", rejection)
          complete(StatusCodes.InternalServerError)
      }
      .result()

  private[this] def makeExceptionHandler: ExceptionHandler =
    ExceptionHandler {
      case ex ⇒
        logger.error("Exception: {}", ex)
        complete(StatusCodes.InternalServerError)
    }

  private[this] def makeLoggingMiddleware: Flow[HttpRequest, HttpRequest, NotUsed] =
    Flow[HttpRequest] alsoTo {
       Sink foreach {
         logger.info("Handling request: {}", _)
       }
    }

  private[this] def makeRequestHandlingContext: Directive0 =
    handleExceptions(makeExceptionHandler) & handleRejections(makeRejectionHandler) & logRequest("A REQUEST")

  private[this] def makeHandlerFlow(http: HttpExt): Flow[HttpRequest, HttpResponse, NotUsed] =
    makeRequestHandlingContext {
      concat(
        makeUnhandled(http),
        makeCompleteFuture
      )
    }

  def start(): Future[Unit] = {
    val conf = config.config.server
    val http = Http()
    val handler = makeLoggingMiddleware.via(makeHandlerFlow(http))
    http
      .bindAndHandle(
        handler = handler,
        interface = conf.host,
        port = conf.port,
      )
      .zip(finish)
      .map { case (binding, _) ⇒ binding }(Now)
      .flatMap(_.unbind())(Now)
  }

}
