package infra
package nineties

import
  com.typesafe.config.{
    Config ⇒ tsConfig,
  }

object config {

  final implicit class Db(val self: tsConfig) extends AnyVal {

    def urlConfigKey        : String                  = self getString "default_url"
    def url                 : String                  = self getString urlConfigKey

  }

  final implicit class Config(val self: tsConfig) extends AnyVal {

    def root                : tsConfig                = self getConfig "infra.nineties"
    def httpServer          : http.server.Config      = root getConfig "http.server"
    def akkaActorSystemName : String                  = root getString "akka_actor_system_name"

    def db                  : Db                      = self getConfig "db"

  }

}
