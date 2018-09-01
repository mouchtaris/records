package samo.list

import xtrm.predef.Certainly

trait Tail[-L, +T] extends Any {
  def unapply(list: L): Certainly[T]
}

