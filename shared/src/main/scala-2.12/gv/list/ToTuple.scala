package gv
package list

trait ToTuple[T <: List, tuple] extends Any {

  def apply(self: T): tuple

}
