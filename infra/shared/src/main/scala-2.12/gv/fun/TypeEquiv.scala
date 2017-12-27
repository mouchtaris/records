package gv
package fun

trait TypeEquiv[a]

object TypeEquiv {

  implicit def definedTypeEquiv[a]: Defined[TypeEquiv[a], a, a] =
    () ⇒ { case a ⇒ a }

}
