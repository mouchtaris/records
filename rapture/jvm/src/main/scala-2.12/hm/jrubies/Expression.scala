package hm.jrubies

final class Expression(val value: String) extends AnyVal {
  override def toString: String = value
}

object Expression {

  def apply(value: String): Expression =
    new Expression(value)

  def string(value: String): Expression =
    new Expression(s"'$value'")

}
