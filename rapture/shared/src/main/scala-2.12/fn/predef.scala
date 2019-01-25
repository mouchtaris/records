package fn

object predef {

  final implicit class Certainly[+A](val get: A) extends AnyVal {
    def isEmpty: Boolean = false
  }

  final type Impl[T] = T
  object Impl {
    def apply[T: Impl]: T = implicitly
    def impl[T <: AnyRef](implicit t: T): t.type = t
  }

}
