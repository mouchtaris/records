package t.fn
package list.reduce

sealed trait Reduce[Zero, F] extends Any with Def[Reduce[Zero, F]]

object Reduce
  extends AnyRef
  with ReduceHighPriority
{
  final implicit class make[Zero, F](val unit: Unit = ()) extends AnyVal with Reduce[Zero, F]
}