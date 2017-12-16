package gv
package string

object ColumnPrinter {

  final implicit class ColumnPrinter(val self: Traversable[(String, Any)]) extends AnyVal {

    private[this] def widths: Traversable[Int] = self.view map (_._1) map (_.length)

    private[this] def maxWidth: Int = widths max

    private[this] def formatName(width: Int)(name: String): String =
      String format (s"%${maxWidth}s: ", name)

    def printTo[w: Writable](w: w): Unit = {
      val width = maxWidth
      val format = formatName(width) _
      for ((name, value) ← self) {
        w ++= format(name)
        w ++= Option(value).toString
        w ++= "\n"
      }
    }

  }

  def apply(self: Traversable[(String, Any)]): ColumnPrinter = self

}


