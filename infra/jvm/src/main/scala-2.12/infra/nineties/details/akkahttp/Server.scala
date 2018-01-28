package infra
package nineties
package details
package akkahttp

import
  akka.actor.{
    ActorSystem,
  },
  akka.stream.{
    Materializer,
    ActorMaterializer,
  },
  akka.http.scaladsl.{
    Http
  }

final case class Server()(
  implicit
  config: Config,
  actorSystem: ActorSystem,
)
{

  private[this] val httpHandler = HttpHandler()
  import httpHandler.handler

  private[this] implicit lazy val materializer: Materializer = ActorMaterializer()

  def bindAndServe() = {
    Http().bindAndHandle(
      handler = handler,
      interface = config.address,
      port = config.port
    )
  }
}
