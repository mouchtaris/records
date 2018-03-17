package infra
package apr
package tpf

trait Apply[tpf <: TPF, x] extends Any {

  type Out

  def apply(x: x): Out

}

object Apply {

  final implicit class Impl[tpf <: TPF, x, out](val self: x => out)
    extends AnyVal
      with (tpf Apply x)
  {

    type Out = out

    def apply(x: x): Out = self(x)

  }

  def apply[tpf <: TPF, x, out](f: x ⇒ out): Impl[tpf, x, out] =
    f

}
