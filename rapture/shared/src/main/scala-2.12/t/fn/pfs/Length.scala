package t
package fn
package pfs

import pf.{ Def, Definition }
import list.{ List, Nil, :: }
import nat.{ Add, Nat }
import predef.I
import predef.I.I

sealed trait Length extends Any with Def[Length]

object Length {

  implicit def nilLength: Definition[Length, Nil, Nat._0] =
    Definition { _ ⇒ Nat._0 }

  implicit def listLength[
    A,
    B <: List,
    LenB: Def[Length]#at[B]#t,
    Len: Def[Add]#at[(LenB, Nat._1)]#t,
  ](
    implicit
    len: Len,
  ): Definition[Length, A :: B, Len] =
    Definition { _ ⇒ len }

}
