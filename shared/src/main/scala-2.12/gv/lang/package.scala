package gv

package object lang {

  type Implicitly[a] = a

  def use[T](t : T): Unit = ()

  final implicit class TapDecoration[T](val self: T) extends AnyVal {
    def tap(f: T ⇒ Unit): T = {
      f(self)
      self
    }
  }

}
