package gv
package string

trait ImplicitWritable[T] extends Any {

  implicit def implicitWritable: Writable[T]

}

