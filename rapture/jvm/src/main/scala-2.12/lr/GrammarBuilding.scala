package lr

import scala.collection.immutable._
import scala.reflect.ClassTag

trait GrammarBuilding extends Any {

  def makeProductions(source: (symbols.Symbol, Iterable[symbols.Symbol])*): ListSet[Production] =
    source
      .map(Production.fromPair)
      .to

  private[this] def collectFromSymbols[S <: symbols.Symbol : ClassTag](syms: Iterable[symbols.Symbol]): ListSet[S] =
    syms
      .collect { case symbol: S ⇒ symbol }
      .to

  private[this] def collectFromProductions[S <: symbols.Symbol : ClassTag](prods: Iterable[Production]): ListSet[S] =
    prods
      .map { case Production(symbol, Expansion(expansion)) ⇒ symbol +: expansion }
      .flatMap(collectFromSymbols[S])
      .to

  implicit def fromProductions(prods: Iterable[Production]): Grammar = {
    val prodsSet: ListSet[Production] = prods.to
    val terminals: ListSet[symbols.Terminal] = collectFromProductions[symbols.Terminal](prodsSet)
    val nonTerminals: ListSet[symbols.NonTerminal] = collectFromProductions[symbols.NonTerminal](prodsSet)
    Grammar(terminals, nonTerminals, prodsSet, nonTerminals.head)
  }
}
