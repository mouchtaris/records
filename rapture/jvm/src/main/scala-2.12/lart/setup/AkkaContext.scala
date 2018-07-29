package lart.setup

import akka.actor.ActorSystem
import akka.stream.{ActorMaterializer, Materializer}

final class AkkaContext(
  implicit
  config: ConfigContext,
) {
  implicit val actorSystem: ActorSystem = ActorSystem(config.config.akkaActors.systemName)
  implicit val materializer: Materializer = ActorMaterializer()(actorSystem)
}
