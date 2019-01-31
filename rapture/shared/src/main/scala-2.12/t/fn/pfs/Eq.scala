package t
package fn
package pfs

sealed trait Eq[A] extends Any with Def[Eq[A]]

object Eq {

  final implicit class Inst[A](val unit: Unit) extends AnyVal with Eq[A]

  implicit def definedEq[A, B](implicit ev: A =:= B): Definition[Eq[A], B, bool.True] =
    Definition { _ ⇒ bool.True }

}
