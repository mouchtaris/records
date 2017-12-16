package gv
package string

trait ImplicitToString[T] extends Any {

  implicit def implicitToString: ToString[T]

}

