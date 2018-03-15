package infra
package nineties
package init

import
  akka.stream.{
    ActorMaterializer,
  }

trait InitStreamSystem {
  this: InitPlace ⇒

  protected[this]
  final type Materializer = akka.stream.Materializer

  implicit val InitStreamSystem: Init[Materializer] =
    () ⇒ actorSystem
      .map { implicit s ⇒ ActorMaterializer() }

}
