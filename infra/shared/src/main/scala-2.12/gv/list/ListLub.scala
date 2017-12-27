package gv
package list

import
  types._

trait ListLub[list <: List, u] extends Any {

  final type U = u

}

object ListLub {

  implicit def lastLub[h]: ListLub[h :: Nil, h] =
    null

  implicit def listLub[h, t <: List, tu, u](
    implicit
    tlub: ListLub[t, tu],
    lub: Lub[h, tu, u]
  ): ListLub[h :: t, u] =
    null

  final implicit class ListLubber[list <: List](val self: Unit) extends AnyVal {
    def apply[u]()(implicit ll: ListLub[list, u]): ListLub[list, u] = ll
  }

  def apply[list <: List]: ListLubber[list] = ()

}


