package t.fn
package list
package length

import pf.{ Def, Pf }
import nat.{ Nat, Add }
import predef.I

sealed trait Length extends Any with Def[Length]

object Length {

  final implicit class NilLengthDefinition(val unit: Unit)
    extends AnyVal
    with Pf[Length, Nil]
  {
    override type Out = nat._0
    override def apply(nil: Nil): Out = nat._0
  }

  implicit def nilLength: NilLengthDefinition = ()

  final implicit class ListLengthDefinition[H, T <: List, R <: Nat](val length: R)
    extends AnyVal
    with Pf[Length, H :: T]
  {
    override type Out = R
    override def apply(in: H :: T): Out = length
  }

  implicit def listLength[
    A,
    B <: List,
    LenB: Def[Length]#at[B]#t,
    Len <: Nat: Def[Add]#at[(LenB, nat._1)]#t: I.I,
  ]: ListLengthDefinition[A, B, Len] =
    new ListLengthDefinition(implicitly)

}
