package fn
package pf

final class ComposeDefinition[F, G, In, A: Def[F]#at[In]#t, B: Def[G]#at[A]#t]
  extends AnyRef
    with Pf[F Compose G, In] {
  override type Out = B

  override def apply(in: In): B = Pf[G](Pf[F](in))
}

