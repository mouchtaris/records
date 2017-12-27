package gv
package string

import
  building._

trait InspectOps[T] extends Any {
  this: ImplicitInspect[T] ⇒

  def self: T

  final def inspectTo[w: Writable](w: w): Unit =
    ColumnPrinter(implicitInspect(self)).printTo(w)

  final def inspect: String =
    BuildingWith[StringBuilder](_.toString)(inspectTo)

}
