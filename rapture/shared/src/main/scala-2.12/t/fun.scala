package t

import scala.language.higherKinds

object fun {

  /**
    * A lightweight function interface -- can be implemented by value classes.
    *
    * @tparam A input type
    * @tparam B output type
    */
  trait -->[-A, +B] extends Any {
    def apply(a: A): B
  }

  /**
    * Applies a typeclass `F[_]` to a given argument.
    *
    * @param unit dummy self
    * @tparam U upper limit for the input type
    * @tparam F type-class type constructor
    * @tparam R result (output) type
    */
  final implicit class Applicator[U, F[-A <: U] <: A --> R, R](val unit: Unit) extends AnyVal {
    def apply[A <: U](a: A)(implicit f: F[A]): R = f(a)
  }

  /**
    * Convenience syntax for writing applicator values.
    *
    * Ex.
    * {{{
    *   trait ToString[T] extends Any with (T --> String) { def apply(self: T): String }
    *   implicit def strToString: ToString[String] = identity
    *   val ToString: ToString ==> String = ()
    *   val str: String = ToString("hello")
    * }}}
    * @tparam F type-class type
    * @tparam R function-call result type
    */
  final type ==>[F[-A] <: A --> R, R] = Applicator[Any, F, R]

  /**
    * Type-class that expresses combining values of type `A` and `B` into
    * type `C`.
    *
    * @tparam A input type 1
    * @tparam B input type 2
    * @tparam C result (combined) type
    */
  trait Combine[-A, -B, +C] extends Any with (A --> (B --> C))

  /**
    * [[t.fun.Combine]] type-class for [[scala.Unit]].
    *
    * Combining Units results in a Unit.
    */
  implicit val combineUnit: Combine[Unit, Unit, Unit] = _ ⇒ _ ⇒ ()

  /**
    * General decoration for lightweight functions.
    * @param self function
    * @tparam A input type
    * @tparam B output type
    */
  final implicit class Deco[A, B](val self: A --> B) extends AnyVal {

    /**
      * Convenience syntax for "distribute".
      *
      * Distributes the input argument to many functions that can accept it.
      *
      * Result types must be implicitly [[t.fun.Combine combinable]].
      *
      * @param another another function
      * @param c result combination evidence
      * @tparam AA input type of `another`
      * @tparam BB result type of `another`
      * @return a function that distributes input argument to `self`
      *         and `another`, and return the [[t.fun.Combine combined]]
      *         result
      */
    def >>[AA >: A, BB](another: AA --> BB)(implicit c: Combine[B, BB, B]): A --> B = a ⇒ c(self(a))(another(a))

  }

}