package t.fn
package list

import predef.Certainly

trait ::[+A, +B <: List] extends Any with List {
  def head: A

  def tail: B

  final override def toString: String =
    s"$head :: $tail"
}

object :: {
  def unapply[A, B <: List](list: A :: B): Certainly[(A, B)] =
    new Certainly((list.head, list.tail))
}
