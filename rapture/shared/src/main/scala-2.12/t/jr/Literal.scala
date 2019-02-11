package t
package jr

import t.fn.predef.{Closed ⇒ fnClosed}

import scala.language.{higherKinds, implicitConversions}

trait Literal[T] extends Any {

  /**
    * Indentation contract: the first line of the literally rendered value
    * produces no extra space.
    *
    * Every literal value being render should know in which indentation level
    * it's been rendered in. For any lines after the first, the value is expected
    * to render the appropriate indentation.
    *
    * @param ind the level the value is being rendered in
    * @param str the literal value code
    * @return the literal value code indented in 2-spaces indentation
    */
  final protected[this] def Ind(ind: Int)(str: String): String = s"${"  " * ind}$str"

  def apply(self: T, ind: Int): String

}

object Literal {

  implicit def stringLiteral: literal.StringLiteral =
    new literal.StringLiteral(())

  implicit def traversableLiteral[
    CC[a] <: Traversable[a],
    T: Literal,
  ]: literal.TraversableLiteral[CC, T] =
    new literal.TraversableLiteral(implicitly)

  implicit def optsLiteral[
    CC[a] <: Traversable[a],
    T: Literal,
  ]: literal.OptsLiteral[CC, T] =
    new literal.OptsLiteral(implicitly)

  implicit def optsMapLiteral[
    CC[a, b] <: Traversable[(a, b)],
    T: Literal,
  ]: literal.OptsMapLiteral[CC, T] =
    new literal.OptsMapLiteral(implicitly)

  def apply[T: Literal](ind: Int)(obj: T): String = implicitly[Literal[T]].apply(obj, ind)
  def apply[T: Literal](obj: T): String = apply(0)(obj)
  def apply[T: Literal]: Literal[T] = implicitly


  final type Closed = fnClosed[Literal]
  object Closed {
    def apply[T: Literal](obj: T): Closed = fnClosed[Literal](obj)
  }

  implicit def closedLiteral: literal.ClosedLiteral =
    new literal.ClosedLiteral(())
}