package infra
package apr
package list_find

import list._
import tpf._

trait ListFind[-L <: List, tpf <: TPF]
  extends AnyRef
    with TPF
{

  type Out

  implicit val ev: tpf Apply Out

  def apply(list: L): Out


}

object ListFind {

  abstract class Impl[L <: List, tpf <: TPF, out](
    val ev: tpf Apply out
  )
    extends AnyRef
      with (L ListFind tpf)
  {
    type Out = out
  }

  implicit def findInHead[h, tpf <: TPF](
    implicit
    tpf: tpf Apply h
  ): ((h :: List) ListFind tpf) { type Out = h } =
    new ((h :: List) ListFind tpf) {
      type Out = h
      val ev = tpf
      def apply(l: h :: List) = l.head
    }

  implicit def findInTail[t <: List, tpf <: TPF](
    implicit
    tev: (t ListFind tpf),
  ): ((_ :: t) ListFind tpf) { type Out = tev.Out } =
    new ((_ :: t) ListFind tpf) {
      type Out = tev.Out
      val ev = tev.ev
      def apply(l: _ :: t): Out = tev(l.tail)
    }


  type Defined[L <: List, tpf <: TPF, out] =
    ((L ListFind tpf) Apply L) {
      type Out = out
    }

  implicit def `(List ListFind TPF) => TPF[ListFind]`[L <: List, tpf <: TPF](
    implicit
    ev: (L ListFind tpf)
  ): Defined[L, tpf, ev.Out] =
    Apply(ev(_))

}
