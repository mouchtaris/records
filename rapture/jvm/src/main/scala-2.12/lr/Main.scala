package lr

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
    final case object S extends NonTerminal
    final case object E extends NonTerminal
    final case object START extends NonTerminal
  }

  final implicit class Expansion(val self: Vector[Vector[Symbol]]) extends AnyVal {
    override def toString: String =
      self
        .map { _ mkString " " }
        .mkString("\n  | ")
  }

  final implicit class Production(val self: (Symbol, Expansion)) extends AnyVal {
    override def toString: String = s"${self._1} → ${self._2}"
  }
  final type SimpleProduction = (Symbol, SimpleExpansion)
  object Productions {
    import NonTerminal._
    import Terminal._
    def apply(): Set[Production] = Set(
      S → Exp(E),
      E → (Exp(E, x, E) | Exp(z)),
    )

    def symProductions(symbol: Symbol): Vector[SimpleProduction] =
      Productions()
        .toVector
        .map(_.self)
        .filter {
          case (`symbol`, _) ⇒ true
          case _ ⇒ false
        }
        .flatMap {
          case (sym, AlternativeExpansions(exps)) ⇒
            exps.map(sym → _)
          case (sym, exp @ SimpleExpansion(_)) ⇒
            Vector(sym → exp)
        }
  }


  final case class ItemExpansion(els: Vector[ItemElement]) {
    override def toString: String = els.mkString(" ")
  }
  final implicit class Item(val self: (Symbol, ItemExpansion)) extends AnyVal {
    override def toString: String = s"${self._1} → ${self._2}"
  }
  object Item {

    final implicit class SimpleExpansionExtension(val self: SimpleExpansion) extends AnyVal {
      def toItem = ItemExpansion(self.symbols)
    }

    def initial(p: SimpleProduction): Item =
      p._1 → ItemExpansion(Cursor +: p._2.toItem.els)

    def initials(sym: Symbol): Vector[Item] =
      Productions
        .symProductions(sym)
        .map(initial)

    def closure(item: Item): Set[Item] = {
      import scala.collection.immutable._
      val exp: Vector[ItemElement] = item.self._2.els
      val clos: Set[Item] = Set(item)
      val idx: Int = exp.indexOf(Cursor: ItemElement)
      if (idx == -1 || idx >= exp.length - 1)
        return clos

      val el: ItemElement = exp(idx)
      if (!el.isInstanceOf[NonTerminal])
        return  clos
      if (!el.isInstanceOf[Symbol])
        throw new Exception("Two cursors?")
      val sym: Symbol = el.asInstanceOf[Symbol]

      val initialItems: Vector[Item] = initials(sym)
      val clos2 = initialItems.foldLeft(clos)(_ + _)
      if (clos.size == clos2.size)
        return clos


    }
  }
  //
  // Sample grammar
  //
  //    S -> E
  //    E ->  E x E
  //        | z
  //
  def main(args: Array[String]): Unit = {
    println(Productions() mkString "\n")
    println(Item.initials(NonTerminal.E) mkString "\n")
  }

}
