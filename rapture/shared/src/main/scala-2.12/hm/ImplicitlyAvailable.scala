package hm

trait ImplicitlyAvailable {

  final implicit def implicitlyAvailable: this.type =
    this

}
