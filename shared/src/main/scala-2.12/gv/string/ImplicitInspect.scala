package gv
package string

trait ImplicitInspect[T] extends Any {

  implicit def implicitInspect: Inspect[T]

}
