package t.fn
package list
package map

import pf.{ Def, Pf }
import select.Select
import length.Length

trait Map[F] extends Any with Def[Map[F]]

object Map {

  final implicit class MapDefinition[F, In, R](val ev: Def[Select[F]]#at[In]#t[R])
    extends AnyVal
    with Pf[Map[F], In]
  {
    override type Out = R
    override def apply(in: In): R = ev(in)
  }

  implicit def mapDefinition[
    F,
    L <: List,
    R: Def[Select[F]]#at[L]#t,
    Len
      : Def[Length]#at[L]#t
      : Def[Length]#at[R]#t,
  ]: MapDefinition[F, L, R] =
    new MapDefinition(implicitly)

  final implicit class Decoration[S](val self: S) extends AnyVal {
    def map[F](f: F)(implicit ev: Pf[Map[F], S]): ev.Out = ev(self)
  }

}
