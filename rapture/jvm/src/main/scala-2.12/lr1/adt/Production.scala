package lr1
package adt

final case class Production(
  symbol: Symbol,
  expansions: Set[Seq[Symbol]]
)
