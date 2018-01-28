package infra
package nineties
package details

import
  com.typesafe.config.{
    Config ⇒ tsConfig,
  }

package object akkahttp {

  final implicit class Config(val root: tsConfig) extends AnyVal {

    def address: String = root getString "bind_address"

    def port: Int = root getInt "bind_port"

  }

}
