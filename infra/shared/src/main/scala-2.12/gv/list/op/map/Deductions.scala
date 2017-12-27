package gv
package list
package op
package map

import
  fun._

trait Deductions extends Any {

  final implicit def nilMap[pf]: Evidence[pf, Nil, Nil] =
    identity[Nil]

  final implicit def listMap[pf, h, t <: List, out, tout <: List](
    implicit
    pf: Defined[pf, h, out],
    tmap: Evidence[pf, t, tout]
  ): Evidence[pf, h :: t, out :: tout] =
    list ⇒ pf(list.head) :: tmap(list.tail)

}
