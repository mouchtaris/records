package t.jr
package literal

import scala.language.higherKinds

final class TraversableLiteral[
  CC[a] <: Traversable[a],
  T,
](
  val ev: Literal[T],
)
  extends AnyVal
    with Literal[CC[T]] {

  override def apply(self: CC[T], ind: Int): String = {
    val pre = "["

    val mid = self
      .map(Literal(ind + 2)(_)(ev))
      .map(Ind(ind + 1))
      .mkString(",\n")

    val aft = Ind(ind)("]")

    s"$pre\n$mid\n$aft"
  }

}
