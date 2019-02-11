package t.jr
package literal

import scala.language.higherKinds

final class OptsMapLiteral[
  CC[a, b] <: Traversable[(a, b)],
  T,
](
  val ev: Literal[T],
)
  extends AnyVal
  with Literal[CC[Symbol, T]]
{

  override def apply(self: CC[Symbol, T], ind: Int): String =
    new OptsLiteral[Traversable, T](ev)(self, ind)

}
