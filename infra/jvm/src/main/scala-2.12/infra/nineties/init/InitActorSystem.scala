package infra
package nineties
package init

import
  akka.actor.{
    ActorSystem,
  }

trait InitActorSystem {
  this: InitPlace ⇒

  protected[this]
  final type ActorSystem = akka.actor.ActorSystem

  implicit val InitActorSystem: init.Init[ActorSystem] =
    () ⇒ config
      .map(_.akkaActorSystemName)
      .map(ActorSystem(_))

}


