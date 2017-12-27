package gv

import
  _root_.slick.{
    lifted,
    ast,
  }

package object slick
  extends AnyRef
{

  type  Rep[T]                  = lifted.Rep[T]
  type  ProvenShape[U]          = lifted.ProvenShape[U]
  val   ProvenShape             = lifted.ProvenShape

  type  TypedType[T]            = ast.TypedType[T]

  type  MappedProjection[t, p]  = lifted.MappedProjection[t, p]
}
