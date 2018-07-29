package hm.jrubies

final case class Env(
  constants: Map[Identifier, Expression],
  statements: Seq[Expression],
)
{
  import RbLine.Lines

  def constants_rb: String =
    constants.toVector
      .map { case (id, expr) ⇒ s"$id = $expr" }
      .map(RbLine(_))
      .toRb

  def statements_rb: String =
    statements.toVector
      .map(_.value)
      .map(RbLine(_))
      .toRb
}

object Env {

  def empty: Env =
    Env(
      constants = Map.empty,
      statements = Seq.empty,
    )

}
