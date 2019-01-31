package t.fn
package list
package reduce

final class ReduceListDefinition[
  Zero,
  F,
  H,
  T <: List,
  RT,
  R,
](
  implicit
  reduce: Def[Reduce[Zero, F]]#at[(Zero, T)]#t[RT],
  f: Def[F]#at[(RT, H)]#t[R],
)
  extends AnyRef
    with Pf[Reduce[Zero, F], (Zero, H :: T)] {

  override type Out = R

  override def apply(in: (Zero, H :: T)): R = in match {
    case (zero, head :: tail) ⇒
      f((reduce((zero, tail)), head))
  }

}
