package lart.setup

class Scene(
  config: hm.config.Config
) {

  val configContext = ConfigContext(config)
  val akkaContext = AkkaContext(configContext)

  val httpServer = new HttpServer(configContext, akkaContext)
}

object Scene {
  def apply(): Scene =
    apply(hm.config())

  def apply(config: hm.config.Config): Scene =
    new Scene(config)
}