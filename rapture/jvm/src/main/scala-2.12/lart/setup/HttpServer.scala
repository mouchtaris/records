package lart
package setup

import akka.actor.ActorSystem
import akka.http.scaladsl.model.Uri.Path
import akka.http.scaladsl.server.{Directives, Route}
import akka.stream.Materializer
import lart.http.routes.{CompleteFuture, Unhandled}
import akka.http.scaladsl.{Http, HttpExt}
import akka.http.scaladsl.model.{HttpRequest, HttpResponse}
import hm.TypeInfo

import scala.concurrent.Future

case object HttpServer
  extends AnyRef
  with TypeInfo[HttpServer]
{

  def completePrefix: String = "stop"

}

class HttpServer(
  config: ConfigContext,
  akka: AkkaContext,
) {
  import HttpServer._

  private[this] val logger = Logger[HttpServer]

  private[this] def makeCompleteFuture: (Route, Future[Unit]) =
    CompleteFuture(
      pathPrefix = completePrefix,
      logger = Logger("<complete future>")
    )

  private[this] def forwardToPat(http: HttpExt): HttpRequest ⇒ Future[HttpResponse] =
    req ⇒ {
      logger.info("forwarding request {}", req)
      val uri2 = req.uri
        .withHost("api.patron.gallery")
        .withScheme("https")
        .withPort(443)
      logger.info("new uri: {}", uri2)
      val req2 = req.withUri(uri2)
      http.singleRequest(req2)
        .andThen { case res ⇒ logger.info("result came in: {}", res) }(Now)
    }

  private[this] def makeUnhandled(http: HttpExt): Route =
    Unhandled(
      handle = forwardToPat(http),
      logger = Logger("<unhandled>")
    )

  def start(): Future[Unit] = {
    implicit val acsys: ActorSystem = akka.actorSystem
    implicit val mat: Materializer = akka.materializer
    val (finishRoute, finish) = makeCompleteFuture
    val http = Http()
    val unhandled = makeUnhandled(http)
    val route = Directives.concat(finishRoute, unhandled)
    val conf = config.config.server
    http
      .bindAndHandle(
        handler = route,
        interface = conf.host,
        port = conf.port,
      )
      .zip(finish)
      .map { case (binding, _) ⇒ binding }(Now)
      .flatMap(_.unbind())(Now)
  }

}
