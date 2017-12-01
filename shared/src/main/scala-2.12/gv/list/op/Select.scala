package gv
package list
package op

import
  fun._

trait Select[pred, list <: List, out <: List] {

  def apply(list: list): out

}

object Select {

  implicit def nilSelect[pred]: Select[pred, Nil, Nil] =
    identity[Nil]

  implicit def notDefinedSelect[pred, h, t <: List, tout <: List](
    implicit
    tsel: Select[pred, t, tout],
    nd: NotDefined[pred, h]
  ): Select[pred, h :: t, tout] =
    list ⇒ tsel(list.tail)

  implicit def definedSelect[pred, h, t <: List, out, tout <: List](
    implicit
    tf: Defined[pred, h, out],
    tsel: Select[pred, t, tout]
  ): Select[pred, h :: t, out :: tout] =
    list ⇒ tf(list.head) :: tsel(list.tail)

  final implicit class SelectWithPredicate[pred](val self: Unit) extends AnyVal {
    def apply[list <: List, out <: List](list: list)(implicit sel: Select[pred, list, out]): out =
      sel(list)
  }

  def apply[pred]: SelectWithPredicate[pred] = ()

}
