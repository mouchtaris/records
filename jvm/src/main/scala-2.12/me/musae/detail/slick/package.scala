package me.musae.detail

import
  _root_.slick.{
    lifted,
    ast,
  },
  gv.{
    list,
    record,
    fun,
  }

package object slick
  extends AnyRef
  with list.ImportPackage
  with record.ImportPackage
  with fun.ImportPackage
{

  type  Rep[T]          = lifted.Rep[T]
  type  ProvenShape[U]  = lifted.ProvenShape[U]
  val   ProvenShape     = lifted.ProvenShape

  type  TypedType[T]    = ast.TypedType[T]

}
