package infra
package nineties
package init

import
  akka.actor.{
    ActorSystem,
  },
  akka.stream.{
    Materializer,
    ActorMaterializer,
  }

final case class AkkaStreamsSystem(
)(
  implicit
  config: Config,
  initActors: Init[ActorSystem]
)
  extends Init[ActorMaterializer]
{

  private[this] lazy val actors = initActors.result

  def initialize(): Result =
    actors map (implicit system ⇒ ActorMaterializer())

}
