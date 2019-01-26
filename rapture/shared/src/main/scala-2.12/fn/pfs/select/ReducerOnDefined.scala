package fn
package pfs.select

import pf.{ Def, Pf }
import list.{ List, :: }

final class ReducerOnDefined[F, Zero <: List, In, R: Def[F]#at[In]#t]
  extends AnyRef
    with Pf[Reducer[F], (Zero, In)] {
  override type Out = R :: Zero

  override def apply(comb: (Zero, In)): Out = comb match {
    case (zero, in) ⇒
      Pf[F](in) :: zero
  }
}

