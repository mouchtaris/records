package me.musae
package model

import
  gv.{
    record,
    list,
  },
  record._,
  list._

object Model {

  object User extends Record {
    type Fields =
      id.type ::
      email.type ::
      Nil

    object id extends int
    object email extends string
  }

}
