package gv
package fun

import
  tag._,
  lang._

trait Named[base]
  extends AnyRef
    with Tagverse
    with ImplicitlyAvailable
    with fun.Type
{
  final type Base = base

  final type T = Tagged[base]

  final class BaseEvidence[f[_]](_ev: f[Base]) {
    type T = Named.this.Base
    val ev: f[T] = _ev
  }

  object BaseEvidence {
    implicit def apply[f[_]](implicit ev: f[Base]): BaseEvidence[f] =
      new BaseEvidence(ev)
  }

  /**
    * OK to override
    * @return
    */
  def name: String = toString

}

object Named {

  type of[t] = Named[_] { type T = t }

}
