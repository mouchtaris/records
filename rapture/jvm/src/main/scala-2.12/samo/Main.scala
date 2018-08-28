package samo

import xtrm.predef.Certainly

object Main {

  object to_string {

    trait ToString[-T] extends Any {
      def apply(obj: T): String
    }

    object ToString {
      final implicit val intToString: ToString[Int] = _.toString
    }

    final implicit class ToStringDecoration[T](val value: T) extends AnyVal {
      def string(implicit toString: ToString[T]): String = toString(value)
    }

  }

  object list {

    sealed trait List extends Any

    trait ::[+H, +T <: List] extends Any with List

    trait Nil extends Any with List

    trait Head[-L <: List, +H] extends Any {
      def unapply(list: L): Certainly[H]
    }

    final implicit class HeadDecoration[L <: List](val value: L) extends AnyVal {
      def head[H](implicit head: L Head H): H = value match {
        case head(h) ⇒ h
      }
    }

    trait Tail[-L <: List, +T <: List] extends Any {
      def unapply(list: L): Certainly[T]
    }

    final implicit class TailDecoration[L <: List](val value: L) extends AnyVal {
      def tail[T <: List](implicit tail: L Tail T): T = value match {
        case tail(t) ⇒ t
      }
    }

    trait Cons[L <: List, H, R <: H :: L] extends Any {
      def apply(list: L, head: H): R
    }

    final implicit class ConsDecoration[L <: List](val value: L) extends AnyVal {
      def ::[H, R <: H :: L](head: H)(implicit cons: Cons[L, H, R]): R = cons(value, head)
    }

    final type WithHead[H] = {
      type λ[L <: List] = L Head H
    }

    final type WithTail[T <: List] = {
      type λ[L <: List] = L Tail T
    }

    object :: {
      def unapply[H, T <: List, L <: List : WithHead[H]#λ : WithTail[T]#λ](list: L): Certainly[(H, T)] =
        (list.head, list.tail)
    }

  }

  object list_to_string {
    import list._
    import to_string._

    final implicit val nilToString: ToString[Nil] = _ ⇒ "Nil"

    final implicit def listToString[H: ToString, T <: List: ToString, L <: List: WithHead[H]#λ: WithTail[T]#λ]: ToString[L] = {
      case h :: t ⇒
        s"${h.string} :: ${t.string}"
    }
  }

  object list_case {
    import list.{
      List,
      Head,
      Tail,
    }

    trait ListCase extends Any with List
    object ListCase {
      implicit def cons[H, L <: ListCase]: list.Cons[L, H, H ::: L] = (list, head) ⇒ :::(head, list)
    }

    final case class :::[+H, +T <: List](_head: H, _tail: T) extends AnyRef with ListCase with list.::[H, T]
    object ::: {
      implicit def head[H]: Head[H ::: _, H] = _._head
      implicit def tail[T <: List]: Tail[_ ::: T, T] = _._tail
    }

    final implicit case object Nil extends AnyRef with ListCase with list.Nil
  }

  def main(args: Array[String]): Unit = {
    import list._
    import list_case._
    import to_string._
    import list_to_string._
    val a = "12" :: 12 :: Nil
    a match {
      case s :: i :: Nil ⇒
        println(s)
        println(i)
        println(Nil)
      case _ ⇒
        println("wat")
    }
    implicitly[ToString[Nil]]
    list_to_string.listToString[Int, Nil, Int ::: Nil]
    println("HJel")
  }
}
