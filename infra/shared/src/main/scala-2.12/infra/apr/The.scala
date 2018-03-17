package infra
package apr

trait The {

  final implicit def implicitlyThis: this.type = this

}

object The {

  def the[t <: AnyRef](implicit t: t): t.type = t

}
