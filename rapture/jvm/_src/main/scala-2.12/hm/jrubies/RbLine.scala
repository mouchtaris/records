package hm.jrubies

final class RbLine(val value: String) extends AnyVal {
  override def toString: String = value
}

object RbLine {

  def apply(value: String): RbLine =
    new RbLine(value)

  final implicit class Lines(val value: Seq[RbLine]) extends AnyVal {
    def toRb: String =
      value map (_.value) mkString "\n"
    override def toString: String =
      toRb
  }
}
