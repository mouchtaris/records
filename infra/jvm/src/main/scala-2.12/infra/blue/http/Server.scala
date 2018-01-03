package infra.blue
package http

import
  scala.util.{
    Success,
  },
  scala.concurrent.{
    Future,
    Promise,
  },
  akka.{
    Done,
    NotUsed,
  },
  akka.actor.{
    ActorSystem,
  },
  akka.stream.{
    Materializer,
  },
  akka.stream.scaladsl.{
    Sink,
  },
  akka.http.scaladsl.server.{
    Directives,
    Route,
  },
  akka.http.scaladsl.model.{
    HttpRequest,
    HttpResponse,
  },
  Directives._,
  gv.{ http }

object Server {

  val route: Route =
    (path("index.html") & get) {
      complete("hi")
    }

  def apply(serveOne: Boolean, routes: Route*)(
    implicit
    config: Config.Ext,
    system: ActorSystem,
    materializer: Materializer,
  ): http.Server = {
    val completeSignal: Promise[NotUsed] = Promise()
    val requestServed: Sink[(HttpRequest, HttpResponse), Future[Done]] =
      Sink.foreach {
        case (req, res) ⇒
          println(s"HTTP\n  $req\n  $res")
          if (serveOne)
            completeSignal.tryComplete(Success(NotUsed))
      }
    implicit val httpConfig = config.toServerConfig
    val route: Route = Server.route ~ routes.reduce(_ ~ _)
    new http.Server(route, completeSignal.future, requestServed)
  }
}

