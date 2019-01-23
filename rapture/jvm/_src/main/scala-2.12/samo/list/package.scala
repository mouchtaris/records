package samo

import xtrm.predef.Certainly

package object list {
  type HeadOf[L] = { type λ[+H] = L Head H}
  type TailOf[L] = { type λ[+T] = L Tail T }
  type List[L] = {
    type Head[+H] = HeadOf[L]#λ[H]
    type Tail[+T] = TailOf[L]#λ[T]
  }

  implicit def toHeadDecoration[L](list: L): HeadDecoration[L] = new HeadDecoration[L](list)
  implicit def toTailDecoration[L](list: L): TailDecoration[L] = new TailDecoration[L](list)

}
