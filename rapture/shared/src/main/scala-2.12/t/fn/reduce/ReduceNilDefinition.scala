package t.fn
package reduce

final class ReduceNilDefinition[Zero, F](val unit: Unit)
  extends AnyVal
    with Pf[Reduce[Zero, F], (Zero, Nil)] {

  override type Out = Zero

  override def apply(in: (Zero, Nil)): Zero = in match {
    case (zero, _) ⇒ zero
  }

}


