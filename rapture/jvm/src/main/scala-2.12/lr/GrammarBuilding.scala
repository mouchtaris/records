package lr

import scala.collection.immutable._
import scala.reflect.ClassTag

trait GrammarBuilding extends Any {

  def makeProductions(source: (symbols.Symbol, Iterable[symbols.Symbol])*): ListSet[Production] =
    source
      .map(Production.fromPair)
      .to

  private[this] def collectFromSymbols[S <: symbols.Symbol : ClassTag](syms: Iterable[symbols.Symbol]): ListSet[S] =
    syms.toVector
      .collect { case symbol: S ⇒ symbol }
      .to

  private[this] def collectFromProductions[S <: symbols.Symbol : ClassTag](prods: Iterable[Production]): ListSet[S] =
    prods.toVector
      .map { case Production(symbol, Expansion(expansion)) ⇒ expansion :+ symbol }
      .flatMap(collectFromSymbols[S])
      .to

  implicit def fromProductions(prods: Iterable[Production]): Grammar = {
    val prodsSet: ListSet[Production] = prods.to
    Grammar(
      collectFromProductions[symbols.Terminal](prodsSet),
      collectFromProductions[symbols.NonTerminal](prodsSet),
      prodsSet,
    )
  }
}
