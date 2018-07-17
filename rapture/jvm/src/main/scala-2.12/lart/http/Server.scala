package lart
package http

import akka.NotUsed
import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.HttpRequest
import akka.http.scaladsl.server.{Directives, Route}
import akka.stream.Materializer
import akka.stream.scaladsl.Flow

import scala.concurrent.{ExecutionContext, Future}
import hm.config.{Server ⇒ Config}
import hm.SomeoneFor.A

final class Server(
  routes: Seq[Route],
  finish: Future[Unit]
)(
  implicit
  materializer: Materializer,
  actorSystem: ActorSystem,
  config: Config,
  executionContext: A[ExecutionContext]#For[Server]
) {
  private[this] implicit def ec: ExecutionContext = executionContext.value
  private[this] def route: Route =
    Directives.concat(routes: _*)

  def bind(): Future[Unit] =
    Http()
      .bindAndHandle(
        handler = route,
        interface = config.host,
        port = config.port
      )
    .zip(finish)
    .map {
      case (binding, unit) ⇒
        unit
    }
}
