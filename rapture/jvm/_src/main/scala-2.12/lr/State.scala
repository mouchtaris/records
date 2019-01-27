package t.lr

import scala.collection.immutable._

final case class State(
  i: Int,
  items: ListSet[Item],
) {

  override def toString: String = {
    val items = this.items map (i ⇒ s"[$i]") mkString " "
    s"I$i: $items"
  }

}

object State extends AnyRef
  with lib.Lensor[State]
{

  object lens {

    def items: Lens[ListSet[Item]] = _.items

  }

}
