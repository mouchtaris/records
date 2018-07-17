package lart.setup

class Scene {

  val configContext = ConfigContext()
  val akkaContext = AkkaContext(configContext)

  val httpServer = new HttpServer(configContext, akkaContext)

}
