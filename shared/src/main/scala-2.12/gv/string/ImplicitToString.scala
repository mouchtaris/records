package gv
package string

trait ImplicitToString[T] {

  implicit def implicitToString: ToString[T]

}

