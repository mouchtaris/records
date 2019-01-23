package xtrm

object get {
  trait Get[-T, +U] extends Any {
    def apply(obj: T): U
  }

  object Get {
    final implicit class Getter[U](val unit: Unit) extends AnyVal {
      def apply[T](adt: T)(implicit get: Get[T, U]): U = get(adt)
      def unapply[T](adt: T)(implicit get: Get[T, U]): predef.Certainly[U] = get(adt)
    }

    def apply[U]: Getter[U] = ()

    final type ValueFrom[U] = {
      type λ[T] = T Get U
    }
  }

}
