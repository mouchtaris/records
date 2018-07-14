package hm

import com.typesafe.config.{
  Config ⇒ tsConfig,
  ConfigFactory,
}

object config {

  trait ConfigObject[T] {
    def name: String
    def construct: tsConfig ⇒ T

    final def apply(config: tsConfig): T = construct(config)
  }

  final implicit class TsConfigDecoration(val value: tsConfig) extends AnyVal {
    def getConfig[T](obj: ConfigObject[T]): T = obj(value getConfig obj.name)
  }

  final object Server extends AnyRef
    with ConfigObject[Server]
  {
    val name = "server"
    val host = "host"
    val port = "port"
    val construct = new Server(_)
  }
  final class Server(val value: tsConfig) extends AnyVal {
    def host: String = value getString Server.host
    def port: Int = value getInt Server.port
  }

  final object Config extends AnyRef
    with ConfigObject[Config]
  {
    val name = "rapture"
    val construct = new Config(_)
  }
  final class Config(val value: tsConfig) extends AnyVal {
    def server = value getConfig Server
  }

  def root = ConfigFactory.defaultApplication
  def apply() = root getConfig Config
}
