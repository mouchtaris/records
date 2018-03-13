package infra
package nineties
package init

import
  akka.actor.{
    ActorSystem,
  }

final case class AkkaActorSystem()(
  implicit
  config: Config
)
  extends Init[ActorSystem]
{

  def initialize(): Result =
    config.result
      .map(_.akkaActorSystemName)
      .map(ActorSystem(_))

  override val close: Close = _.terminate().map(_ ⇒ ())
}
