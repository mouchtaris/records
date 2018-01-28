package infra
package nineties
package init

import
  scala.concurrent.{
    ExecutionContext,
  }

final case class All()(
  implicit
  executionContext: ExecutionContext,
)
{
  implicit val config = init.Config()
  implicit val actors = init.AkkaActorSystem()
  implicit val streams = init.AkkaStreamsSystem()
  implicit val db = init.Database()

  val all = Seq(config, actors, streams, db)

  lazy val components: Future[Components] =
    (config zip actors zip streams zip db).result
      .map {
        case (((conf, actorSystem), actorMaterializer), database) ⇒
          Components(
            conf,
            actorSystem,
            actorMaterializer,
            database
          )
      }

  def cleanUp(): Future[Set[Throwable]] =
    all
      .map(_.cleanUp())
      .foldLeft(Future successful Set.empty[Throwable]) {
        (futureErrors, futureCleanUp) ⇒
          futureErrors.flatMap {
            errors ⇒
              futureCleanUp
                .map { _ ⇒ errors } // on success pass-through errors
                .recover { case ex ⇒ errors + ex } // on failure add error to errors
          }
      }
}
