package hm.jrubies

final class Identifier(val value: String) extends AnyVal {
  override def toString: String = value
}

object Identifier {

  def apply(value: String): Identifier =
    new Identifier(value)

}
