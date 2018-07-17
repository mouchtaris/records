package lart.setup

import akka.actor.ActorSystem
import akka.stream.{ActorMaterializer, Materializer}

final case class AkkaContext(
  actorSystem: ActorSystem,
  materializer: Materializer,
)

object AkkaContext {
  def apply(config: ConfigContext): AkkaContext = {
    val actorSystem = ActorSystem(config.config.akkaActors.systemName)
    val materializer = ActorMaterializer()(actorSystem)
    AkkaContext(actorSystem, materializer)
  }
}
