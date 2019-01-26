package fn.list

final case class Cons[+A, +B <: List](
  override val head: A,
  override val tail: B
)
  extends AnyRef
    with (A :: B)
