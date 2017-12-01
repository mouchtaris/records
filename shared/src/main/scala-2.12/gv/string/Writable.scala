package gv
package string

trait Writable[-T] {

  def writeTo(self: T)(str: String): Unit

}

