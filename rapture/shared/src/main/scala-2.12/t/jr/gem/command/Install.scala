package t.jr
package gem.command

object Install {

  def apply(name: String, version: Option[String]) = Command(
    name = "install_gem",
    opts = version
      .map(params.version → _)
      .foldLeft(Map(params.name → name))(_ + _)
      .map { case (sym, str) ⇒ sym → Literal.Closed(str) }
  )

}
