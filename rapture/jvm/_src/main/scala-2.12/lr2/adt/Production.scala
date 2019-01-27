package lr2
package adt

trait Production[T] extends AnyRef with TypeClass[T] {
  val symbol: R[Closed[Symbol]]
  val expansion: R[t.fns.Reduce[Closed[Symbol]]]
}
