package t.fn
package list.select

final class ReducerOnUndefined[F, Accum, In](val unit: Unit)
  extends AnyVal
    with Pf[Reducer[F], (Accum, In)] {

  override type Out = Accum

  override def apply(comb: (Accum, In)): Out = comb match {
    case (accum, _) ⇒
      accum
  }

}
