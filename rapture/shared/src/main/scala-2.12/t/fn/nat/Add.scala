package t.fn
package nat

import pf.{ Def, Definition }

sealed trait Add extends Any with Def[Add]

object Add {

  implicit def definedAddZero[A <: Nat]: Definition[Add, (A, Zero), A] =
    Definition { case (a, _) ⇒ a }

  implicit def definedAddOne[A <: Nat, B <: Nat](
    implicit
    add: Def[Add]#at[(Succ[A], B)]#pf
  ): Definition[Add, (A, Succ[B]), add.Out] =
    Definition { case (a, Succ(b)) ⇒ add.apply((Succ(a), b)) }

}