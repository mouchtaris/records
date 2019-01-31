package t.fn
package list.select

sealed trait Reducer[F] extends Any with Def[Reducer[F]]

object Reducer
  extends AnyRef
  with ReducerHighPriority
{
  final implicit class apply[F](val unit: Unit) extends AnyVal with Reducer[F]
}