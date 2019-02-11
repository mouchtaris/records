package t.jr
package literal

final class StringLiteral(val unit: Unit) extends AnyVal with Literal[String] {

  private[this] def escape(str: String): String =
    str.replace("{", "\\{")

  override def apply(self: String, ind: Int): String =
    s"%q{${escape(self)}}"

}
