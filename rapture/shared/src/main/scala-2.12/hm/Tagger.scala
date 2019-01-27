package hm

import java.io.Writer

trait Tagger[_base]
  extends Any
{

  trait Tag extends Any {
    final override def toString: String = "lol"
  }

  final type Base = _base
  final type Tagged = Base with Tag

  final def apply(v: Base): Tagged =
    v.asInstanceOf[Tagged]

  final def unapply(o: Any): Option[Base] =
    o match {
      case tagged: Tagged ⇒ Some(tagged)
      case _ ⇒ None
    }

  // Handy aliases
  final type t = Tagged

  final class TaggedTextable extends Textable[Tagged] {
    def writeTo(t: t, w: Writer): Unit = {
      w.write(Tagger.this.toString)
      w.write('[')
      w.write(Tagger.this.unapply(t).get.toString)
      w.write(']')
    }
  }
  implicit def taggedTextable: TaggedTextable = new TaggedTextable
}
