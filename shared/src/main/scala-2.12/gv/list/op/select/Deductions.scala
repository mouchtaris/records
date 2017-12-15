package gv
package list
package op
package select

import
  fun._

trait Deductions extends Any {

  final implicit def nilSelect[pf]: Evidence[pf, Nil, Nil] =
    identity[Nil]

  final implicit def notDefinedSelect[pf, h, t <: List, tout <: List](
    implicit
    tsel: Evidence[pf, t, tout],
    nd: NotDefined[pf, h]
  ): Evidence[pf, h :: t, tout] =
    list ⇒ tsel(list.tail)

  final implicit def definedSelect[pf, h, t <: List, out, tout <: List](
    implicit
    tf: Defined[pf, h, out],
    tsel: Evidence[pf, t, tout]
  ): Evidence[pf, h :: t, out :: tout] =
    list ⇒ tf(list.head) :: tsel(list.tail)

}
