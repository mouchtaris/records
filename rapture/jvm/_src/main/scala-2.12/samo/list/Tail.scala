package samo.list

trait Tail[-L, +T] extends Any {
  def apply(list: L): T
}

