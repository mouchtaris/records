package t.fn.pf

sealed trait Compose[F, G] extends Any

object Compose
  extends AnyRef
  with ComposeHighPriority
{
  /** Instance constructor */
  final implicit class apply[F, G](val unit: Unit) extends AnyVal with Compose[F, G]
}



