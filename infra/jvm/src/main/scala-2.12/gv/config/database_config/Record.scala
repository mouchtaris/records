package gv
package config
package database_config

import
  list._

trait Record extends record.Record {
  case object host extends string
  case object port extends int
  case object user extends string
  case object password extends string
  case object driver extends string
  case object profile extends string

  type Fields =
    host.type ::
    port.type ::
    user.type ::
    password.type ::
    driver.type ::
    profile.type ::
    Nil
}
