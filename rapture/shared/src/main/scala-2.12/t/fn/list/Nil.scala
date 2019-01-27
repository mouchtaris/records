package t.fn
package list

case object Nil extends AnyRef with List {

  override def toString: Predef.String =
    "Nil"

  implicit def implicitNil: Nil = this

}