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

  final object AkkaActors extends AnyRef
    with ConfigObject[AkkaActors]
  {
    def name = "akka_actors"
    def construct = new AkkaActors(_)
    def systemName = "system_name"
  }
  final class AkkaActors(val value: tsConfig) extends AnyVal {
    def systemName: String = value getString AkkaActors.systemName
  }
  final object Server extends AnyRef
    with ConfigObject[Server]
  {
    def name = "server"
    def construct = new Server(_)
    def host = "host"
    def port = "port"
    def forwardScheme = "forwardScheme"
    def forwardPort = "forwardPort"
    def forwardHost = "forwardHost"
  }
  final class Server(val value: tsConfig) extends AnyVal {
    def host: String = value getString Server.host
    def port: Int = value getInt Server.port
    def forwardScheme: String = value getString Server.forwardScheme
    def forwardHost: String = value getString Server.forwardHost
    def forwardPort: Int = value getInt Server.forwardPort
  }

  final object Config extends AnyRef
    with ConfigObject[Config]
  {
    def name = "rapture"
    def construct = new Config(_)
  }
  final class Config(val value: tsConfig) extends AnyVal {
    def server: Server = value getConfig Server
    def akkaActors: AkkaActors = value getConfig AkkaActors
  }

  def root = ConfigFactory.defaultApplication
  def apply() = root getConfig Config
}
