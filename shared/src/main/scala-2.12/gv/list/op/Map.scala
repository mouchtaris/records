package gv
package list
package op

import
  fun._

trait Map[pf, list <: List, out <: List] {

  final type Out = out

  def apply(list: list): Out

}

object Map {

  type resultOf[pf, list <: List] = {
    type λ[out <: List] = Map[pf, list, out]
  }

  implicit def nilMap[pf]: Map[pf, Nil, Nil] =
    identity[Nil]

  implicit def listMap[pf, h, t <: List, out, tout <: List](
    implicit
    pf: Defined[pf, h, out],
    tmap: Map[pf, t, tout]
  ): Map[pf, h :: t, out :: tout] =
    list ⇒ pf(list.head) :: tmap(list.tail)

}


