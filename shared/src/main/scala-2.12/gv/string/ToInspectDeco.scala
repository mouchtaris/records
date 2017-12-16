package gv
package string

trait ToInspectDeco extends Any {

  final implicit def implicitToInspectOps[T: Inspect](_self: T): InspectOps[T] =
    new InspectOps[T]
      with ImplicitInspect[T]
  {
    val self: T = _self
    val implicitInspect: Inspect[T] = implicitly
  }

}
