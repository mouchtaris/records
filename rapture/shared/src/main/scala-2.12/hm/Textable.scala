package hm

import java.io.Writer

trait Textable[-T] {

  def writeTo(obj: T, w: Writer): Unit

}

object Textable {

  final implicit class Ops[T](val self: T) extends AnyRef {
    def writeTo(w: Writer)(implicit txt: Textable[T]): Unit =
      txt.writeTo(self, w)
  }

}