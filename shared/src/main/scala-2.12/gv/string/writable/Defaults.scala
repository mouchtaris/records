package gv
package string
package writable

trait Defaults {

  final implicit object AppendableWritable extends Writable[Appendable] {
    def writeTo(app: Appendable)(str: String): Unit = lang.use {
      app.append(str)
    }
  }

  final implicit object StringBuilderWritable extends Writable[StringBuilder] {
    def writeTo(sb: StringBuilder)(str: String): Unit = lang.use {
      sb.append(str)
    }
  }

}
