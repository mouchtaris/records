package t

import scala.language.higherKinds

object list {

  /**
    * Marker supertype for ADTs of type [[t.list.List List]].
    */
  sealed trait List extends Any

  /**
    * Concrete implementation of the Cons(ecutive) list ADT.
    *
    * [[t.list.:: Cons]] is covariant on both `Head` and `Tail` types,
    * which means more specific Lists can be used in place of more
    * general ones.
    *
    * @param head list head
    * @param tail list tail
    * @tparam Head list head type
    * @tparam Tail list tail type
    */
  final case class ::[+Head, +Tail <: List](head: Head, tail: Tail) extends List

  /**
    * The end of a List ADT.
    */
  object Nil extends AnyRef with List

  /**
    * The type of the end of a List ADT.
    */
  final type Nil = Nil.type

  /**
    * Type-class that expresses pre-pending values to a [[t.list.List]].
    * @tparam L the List's `self` type
    */
  trait Prepend[L <: List] extends Any {
    /**
      * The output type of pre-pending, given the type of `Head` being
      * prepended.
      *
      * @tparam Head the type of `head` being prepended
      */
    type Out[Head] <: List

    /**
      * Prepend `head` to `list`.
      *
      * @param head new head element
      * @param list existing list
      * @tparam Head `head`'s type
      * @return a new list with `head` as the list's head
      */
    def apply[Head](head: Head, list: L): Out[Head]
  }

  /**
    * Default implementation of the [[t.list.Prepend Prepend]] type class,
    * which implements always pre-pending with [[t.list.::]].
    *
    * @tparam L the List's `self` type
    */
  trait PrependWithCons[L <: List] extends Any with Prepend[L] {
    final override type Out[Head] = Head :: L

    final override def apply[Head](head: Head, list: L): Out[Head] = ::(head, list)
  }

  /**
    * Type-class implementation for [[t.list.Prepend Prepend]] for any
    * type of [[t.list.List List]].
    *
    * @param unit just [[scala.Unit]]
    * @tparam L the List's `self` type
    */
  final implicit class PrependToList[L <: List](val unit: Unit) extends AnyVal with PrependWithCons[L]

  /**
    * Implicit provision for [[t.list.PrependToList]].
    * @tparam L list's `self` type
    * @return [[t.list.PrependToList]] instance
    */
  implicit def prependToList[L <: List]: PrependToList[L] = ()

  /**
    * A decoration for types implementing [[t.list.Prepend]].
    *
    * @param self self
    * @tparam L self's type
    */
  final implicit class PrependDecoration[L <: List](val self: L) extends AnyVal {
    def ::[Head](head: Head)(implicit prepend: Prepend[L]): prepend.Out[Head] = prepend(head, self)
  }

  /**
    * Some inline sanity tests and checks.
    */
  object assume {

    trait A extends Any

    trait B extends Any with A

    object A extends A

    object B extends B

    val t1: A :: Nil = A :: Nil
    val t2: A :: B :: Nil = A :: B :: Nil
    val tgen: A :: List = t1
    val tsub: A :: B :: List = t2
    implicitly[Nil <:< List]
    implicitly[(A :: Nil) <:< List]
    implicitly[(B :: List) <:< (A :: List)]
    implicitly[(A :: B :: List) <:< (A :: A :: List)]
    implicitly[(B :: B :: Nil) <:< (A :: List)]
  }

}
