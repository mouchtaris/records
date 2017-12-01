package gv
package string

trait ToString[T] {

  def writeTo[w: Writable](w: w)(obj: T): Unit

}

