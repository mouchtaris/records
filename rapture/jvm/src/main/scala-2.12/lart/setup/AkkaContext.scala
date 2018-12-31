package lart.setup

import akka.actor.ActorSystem
import akka.stream.{ActorMaterializer, Materializer}
import hm.config.Config

final class AkkaContext(
  implicit
  config: Config,
) {
  implicit val actorSystem: ActorSystem = ActorSystem(config.akkaActors.systemName)
  implicit val materializer: Materializer = ActorMaterializer()
}
