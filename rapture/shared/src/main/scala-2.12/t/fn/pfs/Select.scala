package t.fn
package pfs
import t.fn.{ pfs ⇒ pkg }

import pf.{ Pf, Def }
import list.Nil
import reduce.Reduce

sealed trait Select[F] extends Any with Def[Select[F]]

object Select {

  final implicit class apply[F](val unit: Unit) extends AnyVal with pkg.Select[F]

  type Select[F] = Reduce[Nil, select.Reducer[F]]

  final implicit class Decoration[S](val self: S) extends AnyVal {
    def select[F](f: F)(implicit ev: Pf[Select[F], (Nil, S)]): ev.Out = ev((Nil, self))
  }

}
