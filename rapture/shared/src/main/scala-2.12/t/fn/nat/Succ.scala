package t.fn
package nat

import predef.Certainly

final class Succ[N <: Nat](val n: N) extends AnyVal with Nat

object Succ {

  def unapply[N <: Nat](succ: Succ[N]): Certainly[N] =
    new Certainly(succ.n)

  def apply[N <: Nat](n: N): Succ[N] =
    new Succ(n)

}
