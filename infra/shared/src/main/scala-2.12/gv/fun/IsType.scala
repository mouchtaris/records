package gv
package fun

sealed trait IsType[T <: Type] extends Any

object IsType {

  implicit def isType[T <: Type.of[a], a, b <: a]: Defined[IsType[T], b, a] =
    () ⇒ { case b ⇒ b }

  def apply[T <: Type](t: T): IsType[T] = null

}

