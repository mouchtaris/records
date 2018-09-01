package samo.list

import xtrm.predef.Certainly

trait Head[-L, +H] extends Any {
  def unapply(list: L): Certainly[H]
}

