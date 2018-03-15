package infra
package nineties
package details
package akkahttp

import
  scala.concurrent.{
    Future,
  },
  akka.actor.{
    ActorSystem,
  },
  akka.stream.{
    Materializer,
  },
  akka.http.scaladsl.{
    Http
  },
  http.server.{
    Config,
  },
  gv.util.{
    now,
  }

object Server {

  final implicit class Binding(val self: Http.ServerBinding)
    extends AnyVal
    with http.server.Binding
  {
    def unbind(): Future[Unit] =
      self.unbind()
  }

}

final case class Server()(
  implicit
  config: Config,
  actorSystem: ActorSystem,
  materializer: Materializer
)
  extends AnyRef
  with http.server.Server
{

  type Binding = Server.Binding

  private[this] val httpHandler = HttpHandler()
  import httpHandler.handler

  def bind(): Future[Binding] =
    Http()
      .bindAndHandle(
        handler = handler,
        interface = config.address,
        port = config.port
      )
      .map(new Server.Binding(_))(now)
}
