package infra
package apr
package list_map

import list._
import tpf._

trait ListMap[-L <: List, +tpf <: TPF]
  extends Any
  with TPF
{

  type Out <: List

  def apply(list: L): Out

}

object ListMap {

  type Aux[L <: List, tpf <: TPF, out <: List] = (L ListMap tpf) {
    type Out = out
  }

  final implicit class Impl[-L <: List, +tpf <: TPF, +out <: List](val self: L => out)
    extends AnyVal
      with ListMap[L, tpf]
  {

    type Out = out

    def apply(list: L): Out =
      self(list)

  }

  implicit def nilMap[tpf <: TPF]: Aux[Nil, tpf, Nil] =
    (nil: Nil) =>
      nil

  implicit def listMap[h, t <: List, tpf <: TPF](
    implicit
    tpf: (tpf Apply h),
    ev: (t ListMap tpf),
  ): Aux[h :: t, tpf, tpf.Out :: ev.Out] =
    (list: h :: t) =>
      tpf(list.head) :: ev(list.tail)


  type Defined[L <: List, tpf <: TPF, out] =
    (ListMap[L, tpf] Apply L) {
      type Out = out
    }

  implicit def `(List, TPF) => ListMap`[L <: List, tpf <: TPF](
    implicit
    ev: (L ListMap tpf)
  ): ((L ListMap tpf) Apply L) { type Out = ev.Out } =
    Apply(ev(_))
}

