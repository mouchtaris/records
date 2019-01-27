package t.fn
package reduce

sealed trait Reduce[Zero, F] extends Any with Def[Reduce[Zero, F]]

object Reduce
  extends AnyRef
  with ReduceHighPriority
