package t.jr

trait Command extends Any {

  def name: String

  def opts: Map[Param, Literal.Closed]

}

object Command {

  final case class Impl(
    override val name: String,
    override val opts: Map[Param, Literal.Closed],
  )
    extends AnyRef
    with Command

}