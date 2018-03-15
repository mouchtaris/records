package infra

import scala.reflect.runtime.universe._
import java.io.{PrintWriter, Writer}

object tdb {

  object `alias of` {
    def unapply(t: Type): Option[Type] = {
      val dealias = t.dealias
      if (t == dealias && dealias == t)
        None
      else
        Some(dealias)
    }
  }

  final class Indent(val self: Int) extends AnyVal {
    def apply(ps: PrintWriter): ps.type = {
      ps write s"${" |  " * self}"
      ps
    }
    def next = new Indent(self + 1)
  }

  def termOrType(s: Symbol): String =
    if (s.isTerm)
      "term"
    else
      "type"

  def printAnalysis(t: Type, i: Indent, ps: PrintWriter): Unit = {
    val i2 = i.next
    def printf(fmt: String, args: Object*): Unit =
      i2(ps) printf(fmt, args: _*) println()

    printf (" /++ %s", t)

    object fockin {
      val is: AnyRef ⇒ Unit = printf("~~~ It's a focking %s", _)
    }

    def subdo(t: Type) = printAnalysis(t, i2.next, ps)
    t match {
      case `alias of`(u) ⇒
        i2(ps) printf (">>> ALIAS OF %s", u) println()
        subdo(u)
      case RefinedType(_, _) ⇒ fockin is 'refoined
      case ThisType(sym) ⇒
        printf (">>> THIS TYPE for %s", sym)
      case SingleType(pre, sym) ⇒
        printf (">>> SINGLE TOYP of %s isType=%s isTerm=%s" ,
          sym,
          java.lang.Boolean.valueOf(sym.isType),
          java.lang.Boolean.valueOf(sym.isTerm),
        )
        subdo(sym.asTerm.info)
      case NullaryMethodType(result) ⇒
        printf (">>> NULLARY METHOD => %s", result)
        subdo(result)

      case TypeRef(pre, sym, args) ⇒
        printf (">>> %s . %s", pre, sym)
        printf("%s", "...ARGUMENTS")
        args foreach subdo

      case PolyType(typeParams, resultType) ⇒ fockin is 'poly_type
      case RefinedType(_, scope) ⇒ fockin is 'refined_type
      case MethodType(params, result) ⇒ fockin is 'method_type
      case ExistentialType(q, underlying) ⇒ fockin is 'existential_type
      case _ ⇒
        printf ("%s", ">>> SOMETHING ELSE!!!")
    }
    ps flush()
  }

  def tdb(t: Type, indent: Indent = new Indent(0), dest: PrintWriter = new java.io.PrintWriter(System.out)) = printAnalysis(t, indent, dest)

}
