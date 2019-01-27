package t.fn.pf

trait ComposeHighPriority extends Any with ComposeLowPriority {
  implicit def compose[
    F,
    G,
    In,
    A: Def[F]#at[In]#t,
    B: Def[G]#at[A]#t,
  ]: ComposeDefinition[F, G, In, A, B] =
    new ComposeDefinition
}
