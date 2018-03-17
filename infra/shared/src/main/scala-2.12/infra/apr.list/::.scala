package infra
package apr
package list

final case class ::[+a, +b <: List](
  head: a,
  tail: b
)
  extends AnyRef
    with List
{
  override lazy val toString: String =
    s"$head :: $tail"
}

object :: {

  implicit def implicitlyCons[h, t <: List](implicit h: h, t: t): h :: t =
    this(h, t)

}
