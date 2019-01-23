package lart
package setup

import akka.{Done, NotUsed}
import akka.http.scaladsl.model.{HttpRequest, HttpResponse, StatusCodes}
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server._
import akka.http.scaladsl.{Http, HttpExt}
import akka.stream.scaladsl.{Flow, Sink}
import akka.util.ByteString
import hm.config.Config
import hm.{TypeInfo, config}
import lart.http.routes.{RouteCalled, Unhandled}

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
  config: Config,
  akka: AkkaContext,
  logs: LoggingContext,
) {
  import HttpServer._
  import akka.{actorSystem, materializer}

  private[this] val logger: Logging.Logger = logs.factory[HttpServer]
  private[this] val finishPromise: Promise[Unit] = Promise()
  private[this] val finish: Future[Unit] = finishPromise.future
  val conf: hm.config.Server = config.server
  val http = Http()

  logger.info("Good morning")

  private[this] val toPathForwarder: HttpRequest ⇒ Future[HttpResponse] =
    req ⇒ {
      val vec1f = req.entity.dataBytes.runWith(Sink.collection[ByteString, Vector[ByteString]])
      val vec2f = req.entity.dataBytes.runWith(Sink.collection[ByteString, Vector[ByteString]])
      val vecsf = vec1f.zip(vec2f)
      val (vec1, vec2) = Await.result(vecsf, Duration.Inf)
      logger.info("vec1 = {} vec2 = {}", vec1.length, vec2.length)
      val uri2 = req.uri
        .withScheme(conf.forwardScheme)
        .withHost(conf.forwardHost)
        .withPort(conf.forwardPort)
      val req2 = req.withUri(uri2)
      logger.info("forwarding : {}", req2)
      http.singleRequest(req2)
        .andThen { case res ⇒ logger.info("result came in: {}", res) }(Now)
    }

  private[this] val routeCalledRoute: Route =
    RouteCalled(
      pathPrefix = completePrefix,
      completionPromise = finishPromise,
      logger = logs.factory("<complete future>")
    )

  private[this] val unhandledRoute: Route =
    Unhandled(
      handle = toPathForwarder,
      logger = logs.factory("<unhandled>")
    )

  private[this] val rejectionHandler: RejectionHandler =
    RejectionHandler.newBuilder
      .handle {
        case rejection ⇒
          logger.warning("Rejected: {}", rejection)
          complete(StatusCodes.InternalServerError)
      }
      .result()

  private[this] val exceptionHandler: ExceptionHandler =
    ExceptionHandler {
      case ex ⇒
        logger.error("Exception: {}", ex)
        complete(StatusCodes.InternalServerError)
    }

  private[this] val loggingMiddleware: Flow[HttpRequest, HttpRequest, NotUsed] =
    Flow[HttpRequest] alsoTo {
       Sink foreach {
         logger.info("Handling request: {}", _)
       }
    }

  private[this] val requestHandlingContext: Directive0 =
    handleExceptions(exceptionHandler) &
      handleRejections(rejectionHandler) &
      logRequest("A REQUEST")

  val handlerFlow: Flow[HttpRequest, HttpResponse, NotUsed] =
    loggingMiddleware
      .via {
        requestHandlingContext {
          concat(
            unhandledRoute,
            routeCalledRoute
          )
        }
      }

  def start(): Future[Done] = {
    http
      .bindAndHandle(
        handler = handlerFlow,
        interface = conf.host,
        port = conf.port,
      )
      .zip(finish)
      .map { case (binding, _) ⇒ binding }(Now)
      .flatMap(_.unbind())(Now)
  }

}
