package gv
package building


object BuildingWith {

  final implicit class Closure[b](val self: Unit) extends AnyVal {
    def apply[r](r: b ⇒ r)(f: b ⇒ Unit)(implicit ctor: DefaultConstructible[b]): r = {
      val b = ctor.create()
      BuildingWith(b)(r)(f)
    }
  }

  def apply[b]: Closure[b] = ()

  def apply[b, r](b: b)(r: b ⇒ r)(f: b ⇒ Unit): r = {
    f(b)
    r(b)
  }

}

