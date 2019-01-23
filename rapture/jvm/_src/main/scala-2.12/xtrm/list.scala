package xtrm

object list {
  import predef.Certainly

  trait List extends Any

  trait ::[+A, +B <: List] extends Any with List {
    def head: A
    def tail: B

    final override def toString: String =
      s"$head :: $tail"
  }
  object :: {
    def unapply[A, B <: List](list: A :: B): Certainly[(A, B)] = (list.head, list.tail)
  }

  final implicit case object Nil extends List {
    override def toString: Predef.String =
      "Nil"
  }
  final type Nil = Nil.type

  final implicit class ListImplOps[T <: List](val value: T) extends AnyVal {
    def ::[h](h: h): h :: T = Cons(h, value)
  }

  final case class Cons[+A, +B <: List](head: A, tail: B)
    extends AnyRef
    with (A :: B)

  implicit def implicitList[A, B <: List](implicit a: A, b: B): A :: B =
    implicitly[A] :: implicitly[B]
}

