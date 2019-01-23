package lr2
package alg

object Start {

  type RefCount = Map[String, Closed[adt.Symbol]]
  final case class State(
    refCount: RefCount,
    grammar: Iterator[Closed[adt.Production]],
  )

}
