package t.lr

import scala.collection.immutable._

final case class Grammar(
  N: ListSet[symbols.Terminal],
  Σ: ListSet[symbols.NonTerminal],
  P: ListSet[Production],
  start: symbols.NonTerminal,
) {

  override def toString: String =
    P mkString "\n"

}

object Grammar extends AnyRef
  with GrammarBuilding
{

  final implicit class GrammarOps(val self: Grammar) extends AnyVal {

    def apply(symbol: symbols.Symbol): ListSet[Production] =
      self.P.filter(_.is(symbol))

    def symbolTable: Map[symbols.Symbol, Int] =
      (self.N ++ self.Σ).zipWithIndex.toMap

    def productionTable: Map[Production, Int] =
      self.P.zipWithIndex.toMap

  }

}
