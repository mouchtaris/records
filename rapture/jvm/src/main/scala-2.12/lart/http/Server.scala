package lart
package http

import akka.NotUsed
import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.HttpRequest
import akka.http.scaladsl.server.{Directives, Route}
import akka.stream.Materializer
import akka.stream.scaladsl.Flow

import scala.concurrent.Future
import hm.config.{Server ⇒ Config}

final class Server(
  routes: Seq[Route],
)(
  implicit
  materializer: Materializer,
  actorSystem: ActorSystem,
  config: Config,
) {
  private[this] def route: Route =
    Directives.concat(routes: _*)

  def bind(): Future[Http.ServerBinding] =
    Http()
      .bindAndHandle(
        handler = route,
        interface = config.host,
        port = config.port
      )
}
