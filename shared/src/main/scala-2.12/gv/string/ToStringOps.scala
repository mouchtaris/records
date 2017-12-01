package gv
package string

import
  building._

trait ToStringOps[T] {
  this: ImplicitToString[T] ⇒

  def self: T

  final def writeTo[w: Writable](w: w): Unit =
    implicitToString.writeTo(w)(self)

  final def to_str: String =
    BuildingWith[StringBuilder](_.toString)(writeTo)

  final def print(): Unit =
    writeTo(System.out)

  final def println(): Unit = {
    print()
    System.out.println()
  }

}

