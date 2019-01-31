package t.fn
package pf.compose

sealed trait Compose[F, G] extends Any

object Compose {
  /** Instance constructor */
  final implicit class apply[F, G](val unit: Unit) extends AnyVal with Compose[F, G]
}



