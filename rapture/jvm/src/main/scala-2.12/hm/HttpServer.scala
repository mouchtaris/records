package hm

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.stream.Materializer
import akka.http.scaladsl.server._
import akka.http.scaladsl.server.Directives._

import scala.concurrent.Future

class HttpServer(
  implicit
  actorSystem: ActorSystem,
  materializer: Materializer
) {

  val http = Http()
  val route: Route =
    complete("Helllo")
  val binding: Future[Http.ServerBinding] = http.bindAndHandle(
    handler = route,
    interface = "0.0.0.0",
    port = 9000,
  )

}
