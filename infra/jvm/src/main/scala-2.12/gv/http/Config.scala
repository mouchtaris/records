package gv
package http

import
  com.typesafe.config.{
    Config ⇒ tsConfig,
  }

object Config {

  final val INTERFACE = "interface"
  final val PORT = "port"

  final implicit class Ext(
    val root: tsConfig
  ) {
    def interface: String = root getString INTERFACE
    def port: Int = root getInt PORT
  }

}
