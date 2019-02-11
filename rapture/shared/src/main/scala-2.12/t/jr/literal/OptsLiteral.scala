package t.jr
package literal

final class OptsLiteral[
  CC[a] <: Traversable[a],
  T,
](
  val ev: Literal[T],
)
extends AnyVal
  with Literal[CC[(Symbol, T)]] {


  override def apply(self: CC[(Symbol, T)], ind: Int): String = {

    def formatPair(p: (Symbol, T)): String =
    p match {
      case (sym, obj) ⇒
        s"${sym.name}: ${Literal(ind + 1)(obj)(ev)}"
    }

    self
      .map(formatPair)
      .mkString(",\n")

  }

}
