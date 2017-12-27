package gv
package config
package database_config

import
  com.typesafe.config.{
    Config,
  },
  record._

trait FromDatabaseConfigConstructor extends AnyRef {
  import DatabaseConfig.{
    host,
    port,
    user,
    password,
    driver,
    profile,
  }

  val create: Config ⇒ DatabaseConfig.Closed = {
    ???
  }

}
