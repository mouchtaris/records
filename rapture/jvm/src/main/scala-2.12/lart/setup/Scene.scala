package lart.setup

import akka.actor.ActorRef

class Scene(
  implicit
  config: hm.config.Config
) {

  implicit val akkaContext: AkkaContext = new AkkaContext
  implicit val loggingContext: LoggingContext = new LoggingContext

  val httpServer = new HttpServer
  def tcpHandler: ActorRef = new TcpListener().handler
}

object Scene {

  def apply(): Scene = {
    implicit val config: hm.config.Config = hm.config()
    new Scene
  }

}