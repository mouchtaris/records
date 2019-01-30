package t.fn
package pf

trait Def[F] extends Any {

  sealed trait at[In] extends Any {
    type t[R] = Pf[F, In] {type Out = R}
    type pf = Pf[F, In]
  }

}

object Def {
  def apply[F, In]: Definer[F, In] = new Definer(())
}

