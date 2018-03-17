package infra
package nineties
package init

import
  akka.actor.{
    ActorSystem,
  }

trait InitActorSystemMixin {
  this: InitPlace ⇒

  protected[this]
  final type ActorSystem = akka.actor.ActorSystem

  final type InitActorSystem = init.Init[ActorSystem]
  final implicit val InitActorSystem: InitActorSystem =
    () ⇒ config
      .map(_.akkaActorSystemName)
      .map(ActorSystem(_))

}


