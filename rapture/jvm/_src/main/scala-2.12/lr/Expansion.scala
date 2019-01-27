package t.lr

final case class Expansion(value: Vector[symbols.Symbol]) {

  override def toString: String = value mkString " "

}

object Expansion {

  implicit def fromIterable(iterable: Iterable[symbols.Symbol]): Expansion =
    Expansion(iterable.toVector)

}


