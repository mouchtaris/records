package gv
package lang

import
  building._

object ThrowableExt {

  final case class StringBuilderWriter(
    b: StringBuilder
  )
    extends java.io.Writer
  {
    def flush(): Unit = ()
    def close(): Unit = ()
    def write(cbuf: Array[Char], off: Int, len: Int): Unit =
      b.appendAll(cbuf, off, len)
  }

  implicit def stringBuilderToPrintWriter(sb: StringBuilder): java.io.PrintWriter =
    new java.io.PrintWriter(StringBuilderWriter(sb))

  trait Decoration extends Any {

    def self: Throwable

    final def stackTrace: String =
      BuildingWith[StringBuilder](_.result()) { b ⇒
        self.printStackTrace(b)
      }
  }

}
