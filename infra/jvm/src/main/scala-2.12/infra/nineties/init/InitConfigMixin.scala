package infra
package nineties
package init

import
  _root_.infra.{
    nineties ⇒ app
  },
  scala.concurrent.{
    Future,
  },
  com.typesafe.config.{
    ConfigFactory,
  }

trait InitConfigMixin {
  this: InitPlace ⇒

  final type InitConfig = init.Init[app.config.Config]

  final val InitConfig: InitConfig = {
    println(" ****** INITRACK: Init Config evidence ")
    () ⇒ {
      println(" ****** INITRACK create config promise")
      Future {
        println(" ****** INITRACK create config begin")
        val result = ConfigFactory.defaultApplication()
        println(" ****** INITRACK create config end")
        result
      }
    }
  }

}
