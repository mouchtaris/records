package t.jr
package literal

final class ClosedLiteral(val unit: Unit) extends AnyVal with Literal[Literal.Closed] {

  override def apply(self: Literal.Closed, ind: Int): String =
    self(_(_, ind))

}
