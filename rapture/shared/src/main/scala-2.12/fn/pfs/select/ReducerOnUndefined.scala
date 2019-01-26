package fn
package pfs.select

import pf.Pf

final class ReducerOnUndefined[F, Zero, In](val unit: Unit)
  extends AnyVal
    with Pf[Reducer[F], (Zero, In)] {
  override type Out = Zero

  override def apply(comb: (Zero, In)): Out = comb match {
    case (zero, _) ⇒
      zero
  }
}
