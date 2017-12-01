package gv
package string

trait WritableOps[T] extends Any {
  this: ImplicitWritable[T] ⇒

  def self: T

  final def ++=(str: String): T = {
    val s = self
    implicitWritable.writeTo(s)(str)
    s
  }

  final def print(str: String): Unit =
    this ++= str

  final def println(str: String): Unit = {
    this ++= str ++= "\n"
  }

}

