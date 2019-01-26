package fn.pf

import scala.annotation.implicitNotFound

@implicitNotFound(msg = "Pf ${F} not defined at ${In}")
trait Pf[-F, -In] extends Any {
  type Out

  def apply(in: In): Out
}

object Pf {
  def apply[F]: Applicator[F] = new Applicator(())
}