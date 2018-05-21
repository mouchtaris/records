package hm

import com.typesafe.config.{
  Config ⇒ tsConfig,
  ConfigFactory,
}

object config {

  final val Rapture = "rapture"

  final object Server {
    val name = "server"
    val host = "host"
    val port = "port"
  }
  final class Server(val self: tsConfig) extends AnyVal {
    def host: String = self.getString(Server.host)
    def port: Int = self.getInt(Server.port)
  }

  final class Config(val self: tsConfig) extends AnyVal {
    def server: Server = new Server(self.getConfig(Server.name))
  }
  final object Config {
    val name = "rapture"
  }

  def apply(): Config =
    new Config(ConfigFactory.defaultApplication.getConfig(Rapture))

}
