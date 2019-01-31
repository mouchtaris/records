package t.fn
package list
package select

final class ReducerOnDefined[
  F,
  Accum <: List,
  In,
  R,
](
  val f: Def[F]#at[In]#t[R],
)
  extends AnyVal
    with Pf[Reducer[F], (Accum, In)] {

  override type Out = R :: Accum

  override def apply(comb: (Accum, In)): Out = comb match {
    case (accum, in) ⇒
      f(in) :: accum
  }

}

