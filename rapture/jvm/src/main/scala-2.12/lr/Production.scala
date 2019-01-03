package lr

import scala.collection.immutable._

final case class Production(
  symbol: symbols.Symbol,
  expansion: Expansion,
) {

  override def toString: String = s"$symbol → $expansion"

}

object Production {

  implicit def fromPair(pair: (symbols.Symbol, Iterable[symbols.Symbol])): Production =
    Production(pair._1, pair._2)


  final implicit class ProductionDecoration(val self: Production) extends AnyVal {

    def is(symbol: symbols.Symbol): Boolean =symbol == self.symbol

  }

}


