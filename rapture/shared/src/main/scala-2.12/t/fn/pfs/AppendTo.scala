package t.fn
package pfs

import pf.{ Def, Definition }
import list.{ List, :: }

trait AppendTo extends Any with Def[AppendTo]

object AppendTo extends AnyRef with AppendTo {

  implicit def appendToDefinition[H, T <: List]: Definition[AppendTo, (T, H), H :: T] = Definition {
    case (tail, head) ⇒
      head :: tail
  }

  final implicit class Decoration[S](val self: S) extends AnyVal {
    def appendTo[T <: List, R](tail: T)(implicit ev: AppendTo#at[(T, S)]#t[R]): R = ev((tail, self))
  }

}
