package incubator

import
  gv._,
  string._

object typebug {
  import scala.reflect.runtime.universe._

  final implicit class Ind(val n: Int) extends AnyVal {
    def prefix: String = "| " * n
    def apply(any: Any): String = prefix ++ any.toString
    def +(m: Int): Ind = n + m
  }
  final implicit class Header(val str: String) extends AnyVal {
    override def toString: String = str
  }

  trait Matchation[in, out] {
    final type F = in ⇒ Option[out]
    protected[this] val f: F
    final def unapply(in: in): Option[out] =
      f(in)
  }

  def ifm[in, out, r](m: Matchation[in, out])(in: in)(f: out ⇒ Unit): Unit =
    m unapply in foreach f

  object Alias extends Matchation[Type, Type] {
    val f: F = { t ⇒
      val dealiased = t.dealias
      if (dealiased == t)
        None
      else
        Some(dealiased)
    }
  }

  object Methoder extends Matchation[Type, Type] {
    val f: F = {
      case MethodType(_, res) ⇒ Some(res)
      case NullaryMethodType(res) ⇒ Some(res)
      case _ ⇒ None
    }
  }

  object sinks {
    implicit val out = {
      val sonk = Console.out
      val wr = implicitly[Writable[sonk.type]]
      new TypeBugInspectSink(wr writeTo sonk)
    }
  }

  final implicit class TypeBugInspectSink(val self: String ⇒ Unit) extends AnyVal
  object TypeBugInspectSink {
    implicit val typeBugInspectionSinkWritable: Writable[TypeBugInspectSink] =
      new Writable[TypeBugInspectSink] {
        def writeTo(self: TypeBugInspectSink)(str: String): Unit =
          self.self(str)
      }
  }

  def inspect[t: TypeTag](implicit sink: TypeBugInspectSink): Unit =
    inspect(sink, typeOf[t])

  def inspect[w: Writable](w: w, t: Type, ind0: Ind = Ind(0), head0: Header = Header("")): Unit = {
    val ind1 = ind0 + 1
    w.print(ind0(head0))
    w.println(t.toString)

    t.typeArgs.zipWithIndex.foreach {
      case (ta, i) ⇒
        val head1: Header = s"[$i] "
        inspect(w, ta, ind1, head1)
    }

    ifm(Methoder)(t)(inspect(w, _, ind1, "=> ": Header))

    t match {
      case Alias(deal) ⇒
        val head1: Header = "= "
        inspect(w, deal, ind1, head1)
      case SingleType(_, sym) ⇒
        val head1: Header = "<: "
        val inf = sym.info
        inspect(w, inf, ind1, head1)
      case _ ⇒
    }
  }

}
