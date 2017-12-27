package gv
package list
package op
package get

import
  fun._

trait Functor extends Any {

  final def apply[
    t <: Type: gettableFrom[list]#λ,
    list <: List,
  ](t: t)(list: list)(implicit itsok: t#T <:< t.T): t.T =
    list get t

}
