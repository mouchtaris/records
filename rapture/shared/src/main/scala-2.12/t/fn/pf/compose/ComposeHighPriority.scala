package t.fn
package pf
package compose

trait ComposeHighPriority extends Any with ComposeLowPriority {
  implicit def compose[
    F,
    G,
    A,
    B: Def[F]#at[A]#t,
    C: Def[G]#at[B]#t,
  ]: ComposeDefinition[F, G, A, B, C] =
    new ComposeDefinition
}
