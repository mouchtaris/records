package infra
package nineties
package init

import
  akka.stream.{
    ActorMaterializer,
  }

trait InitStreamSystemMixin {
  this: InitPlace ⇒

  protected[this]
  final type Materializer = akka.stream.Materializer

  final type InitStreamSystem = Init[Materializer]
  final implicit val InitStreamSystem: InitStreamSystem =
    () ⇒ actorSystem
      .map { implicit s ⇒ ActorMaterializer() }

}
