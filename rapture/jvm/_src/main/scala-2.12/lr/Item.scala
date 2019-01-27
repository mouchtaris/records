package t.lr

import scala.collection.immutable._

final case class Item(
  production: Production,
  cursorIndex: Int,
) {

  override def toString: String = {
    val symbol = this.symbol
    val preCursor = this.preCursor
    val postCursor = this.postCursor
    s"$symbol → $preCursor $Cursor $postCursor"
  }

}

object Item extends AnyRef
  with lib.Lensor[Item]
{

  final implicit class ItemOps(val self: Item) extends AnyVal {

    def production: Production = self.production

    def cursorIndex: Int = self.cursorIndex

    def symbol: symbols.Symbol = production.symbol

    def expansion: Vector[symbols.Symbol] = production.expansion.value

    def preCursor = Expansion(expansion.slice(0, cursorIndex))

    def postCursor: Expansion = {
      val expansion = this.expansion
      Expansion(expansion.slice(cursorIndex, expansion.size))
    }

    // def cursor: Symbol = expansion(cursorIndex)
    def cursorOpt: Option[symbols.Symbol] = expansion.lift.apply(cursorIndex)

    def isFinal: Boolean = cursorOpt.isEmpty
  }

  object lens {

    val symbol: Lens[symbols.Symbol] = _.symbol

  }

  def initial(production: Production): Item =
    Item(production, 0)

}
