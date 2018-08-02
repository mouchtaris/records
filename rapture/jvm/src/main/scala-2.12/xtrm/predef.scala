package xtrm

object predef {

  final implicit class Certainly[A](val get: A) extends AnyVal {
    def isEmpty: Boolean = false
  }

  object && {
    def unapply[T](obj: T): Certainly[(T, T)] = (obj, obj)
  }

}
