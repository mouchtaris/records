package t.fn
package list
package appendTo

import pf.{ Def, Pf }

sealed trait AppendTo extends Any with Def[AppendTo]

object AppendTo extends AnyRef with AppendTo {

  final implicit class make(val unit: Unit = ()) extends AnyVal with AppendTo

  final implicit class Definition[H, T <: List](val unit: Unit)
    extends AnyVal
    with Pf[AppendTo, (T, H)]
  {
    override type Out = H :: T

    override def apply(in: (T, H)): Out = in match {
      case (tail, head) ⇒
        head :: tail
    }
  }

  implicit def appendToDefinition[H, T <: List]: Definition[H, T] = ()

  final implicit class Decoration[S](val self: S) extends AnyVal {
    def appendTo[T <: List, R](tail: T)(implicit ev: AppendTo#at[(T, S)]#t[R]): R = ev((tail, self))
  }

}
