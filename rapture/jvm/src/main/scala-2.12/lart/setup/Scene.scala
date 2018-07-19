package lart.setup

import akka.actor.ActorRef

class Scene(
  config: hm.config.Config
) {

  val configContext = ConfigContext(config)
  val akkaContext = AkkaContext(configContext)
  val loggingContext = LoggingContext(akkaContext)

  val httpServer = new HttpServer(configContext, akkaContext, loggingContext)
  def tcpHandler: ActorRef = new TcpListener(akkaContext, loggingContext).handler
}

object Scene {
  def apply(): Scene =
    apply(hm.config())

  def apply(config: hm.config.Config): Scene =
    new Scene(config)
}