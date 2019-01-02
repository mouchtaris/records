package lr
import scala.collection.IterableView
import scala.collection.immutable._

object Main {
  final case object Cursor {
    override def toString: String = "•"
  }

  sealed trait Symbol extends Any
  sealed trait Terminal extends Any with Symbol
  object Terminal {
    final case object x extends Terminal
    final case object z extends Terminal
    final case object e extends Terminal
    final case object EOS extends Terminal
  }
  sealed trait NonTerminal extends Any with Symbol
  object NonTerminal {
    final case object `S'` extends NonTerminal
    final case object S extends NonTerminal
    final case object E extends NonTerminal
    final case object START extends NonTerminal
  }

  final type Production = (Symbol, Vector[Symbol])
  final implicit class ProductionDecoration(val self: Production) extends AnyVal {
    def symbol: Symbol = self._1
    def expansion: Vector[Symbol] = self._2
    override def toString: String = self match { case (s, p) ⇒ s"$s → ${p mkString " "}" }
  }
  object Productions {

    import Terminal._
    import NonTerminal._

    val P = Vector

    val all: Vector[Production] = P(
      `S'` → P(S),
      S → P(E),
      E → P(E, x, E),
      E → P(z),
    )
    val decorated: Vector[ProductionDecoration] = all map (new ProductionDecoration(_))

    val forSymbol: Symbol ⇒ Vector[Production] = sym ⇒ all filter { _._1 == sym }
  }

  final implicit class BoolDeco(val self: Boolean) extends AnyVal {
    def ifTrue[T](obj: ⇒ T): Option[T] = if (self) Some(obj) else None
  }

  final case class Item(prod: Production, cursorIndex: Int) {
    def closing: Option[Symbol] =
      (cursorIndex < prod._2.length)
        .ifTrue { prod._2(cursorIndex) }
        .collect { case nonTerminal: NonTerminal ⇒ nonTerminal }
    def preCursor: Vector[Symbol] = prod.expansion.slice(0, cursorIndex)
    def postCursor: Vector[Symbol] = prod.expansion.slice(cursorIndex, prod.expansion.size)
    override def toString: String = s"${prod.symbol} → ${preCursor mkString " "} $Cursor ${postCursor mkString " "}"
  }
  object Items {

    final implicit class ProductionDecoration(val self: Production) extends AnyVal {
      def initialItem: Item = Item(self, 0)
    }

    val itemClosure: Item ⇒ Set[Item] =
      item ⇒
        item.closing
          .map(Productions.forSymbol).getOrElse(Vector.empty)
          .map(_.initialItem)
          .foldLeft(Set(item))(_ + _)

    val closure: Set[Item] ⇒ Set[Item] =
      set ⇒ {
        val set2 = set flatMap itemClosure
        (set == set2)
          .ifTrue(set)
          .getOrElse(closure(set2))
      }

    val symbolClosure: Symbol ⇒ Set[Item] =
      Productions.forSymbol andThen (_.map(_.initialItem).toSet.flatMap(itemClosure andThen closure))
    val productionClosure: Production ⇒ Set[Item] =
      ((_: Production).symbol) andThen symbolClosure
  }
  //
  // Sample grammar
  //
  //    S -> E
  //    E ->  E x E
  //        | z
  //
  def main(args: Array[String]): Unit = {
    println(Productions.decorated mkString "\n")
    println(Items.symbolClosure(NonTerminal.`S'`).mkString("\n"))
  }

}
