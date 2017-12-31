package infra.blue
package http

import
  com.typesafe.config.{
    Config ⇒ tsConfig,
  },
  gv.{ http }

object Config {

  final val CONFIG = "infra.blue.http"

  final implicit class Ext(
    val self: tsConfig
  ) extends AnyVal
  {
    def root: tsConfig = self getConfig CONFIG
    def toServerConfig: http.Config.Ext = root
  }

}
