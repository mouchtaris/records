package infra.blue

import
  gv.{
    list,
    record,
  },
  list._,
  record._

trait model {

  case object account extends Record {
    case object id extends int
    case object email extends string

    type Fields =
      id.type ::
      email.type ::
      Nil
  }

  case object users extends Record {
    type Fields =
      id.type ::
      email.type ::
      `type`.type ::
      Nil

    case object id extends int
    case object email extends string
    case object `type` extends string
  }

}

object model extends model