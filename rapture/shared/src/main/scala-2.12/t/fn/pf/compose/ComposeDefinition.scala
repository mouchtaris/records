package t.fn
package pf
package compose

final class ComposeDefinition[
  F,
  G,
  A,
  B,
  C,
](
  implicit
  f: Def[F]#at[A]#t[B],
  g: Def[G]#at[B]#t[C],
)
  extends AnyRef
    with Pf[F Compose G, A] {

  override type Out = C

  override def apply(a: A): C = g(f(a))

}