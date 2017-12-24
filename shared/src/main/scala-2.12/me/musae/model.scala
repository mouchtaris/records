package me
package musae

import
  gv.{
    list,
    record,
  },
  list._,
  record._

trait model {

  case object user extends Record {
    type Fields =
      id.type ::
      email.type ::
      Nil

    case object id extends int
    case object email extends string
  }

}

object model extends model
