package gv
package list

trait ToList[T, list <: List] extends Any {

  def apply(self: T): list

}
