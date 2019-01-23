package samo.list

trait Head[-L, +H] extends Any {
  def apply(list: L): H
}

