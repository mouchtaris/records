package lr

import scala.collection.immutable._
import scala.reflect.ClassTag

final case class Grammar(
  N: ListSet[symbols.Terminal],
  Σ: ListSet[symbols.NonTerminal],
  P: ListSet[Production],
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

  }

}
