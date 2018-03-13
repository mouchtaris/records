package infra
package nineties
package init

import
  details.akkahttp.{
    Server,
  }

final case class HttpServer()(
  implicit
  config: Config,
  akkaSystem: Init[akka.actor.ActorSystem]
)
  extends Init[Server]
{

  private[this] implicit val akkaHttpConfig = config map (_.akkahttp)

  def initialize(): Result =
    akkaHttpConfig flatMap { implicit akkaHttpConfig ⇒
      akkaSystem map { implicit actorSystem ⇒
        Server()
      }
    }

}
