package gv
package list
package op
package fold

import
  scala.reflect.{ ClassTag },
  fun._

trait Deductions extends Any {

  final implicit def fold2[
    pf,
    a <: in: ClassTag,
    b <: in: ClassTag,
    aout,
    bout,
    in,
    out,
  ](
    implicit
    pfa: Defined[pf, a, aout],
    pfb: Defined[pf, b, bout],
    eva: aout ⇒ out,
    evb: bout ⇒ out,
  ): Evidence[pf, a :: b :: Nil, in, out] = {
    case a: a ⇒
      eva(pfa()(a))
    case b: b ⇒
      evb(pfb()(b))
  }

  final implicit def foldList[
    pf,
    h <: in: ClassTag,
    t <: List,
    hout,
    in,
    out
  ](
    implicit
    pfh: Defined[pf, h, hout],
    tfold: Evidence[pf, t, in, out],
    evh: hout ⇒ out,
  ): Evidence[pf, h :: t, in, out] = {
    case h: h ⇒
      evh(pfh()(h))
    case other ⇒
      tfold(other)
  }

}
