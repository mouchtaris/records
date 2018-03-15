package infra
package nineties
package http

import
  scala.concurrent.{
    Future,
  },
  com.typesafe.config.{
    Config ⇒ tsConfig,
  }

package object server {

  final implicit class Config(val root: tsConfig) extends AnyVal {

    def address: String = root getString "bind_address"

    def port: Int = root getInt "bind_port"

  }

  trait Binding extends Any {

    def unbind(): Future[Unit]

  }

  trait Server {

    type Binding <: server.Binding

    def bind(): Future[Binding]

  }

}
