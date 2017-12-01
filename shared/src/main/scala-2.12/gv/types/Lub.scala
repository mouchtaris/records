package gv
package types

trait Lub[a, b, u] extends Any {

  final type U = u

  def au(a: a): U

  def bu(b: b): U

}

object Lub {

  type of[a, b] = {
    type λ[u] = Lub[a, b, u]
  }

  implicit def implicitLub[a <: u, b <: u, u]: Lub[a, b, u] =
    new Lub[a, b, u] {
      def au(a: a): u = a
      def bu(b: b): u = b
    }

  final implicit class Lubber[a, b](val self: Unit) extends AnyVal {
    def apply[la >: a <: u, lb >: b <: u, u](): Lub[a, b, u] =
      new Lub[a, b, u] {
        def au(a: a): u = a: la
        def bu(b: b): u = b: lb
      }
  }

  def apply[a, b]: Lubber[a, b] = ()

}


