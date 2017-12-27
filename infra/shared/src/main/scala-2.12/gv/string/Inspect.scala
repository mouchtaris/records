package gv
package string

trait Inspect[T] extends Any {

  def apply(t: T): Stream[(String, Any)]

}
