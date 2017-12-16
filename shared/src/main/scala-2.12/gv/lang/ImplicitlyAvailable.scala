package gv
package lang

trait ImplicitlyAvailable extends AnyRef {

  final implicit def implicitlyAvailable: this.type =
    this

}


