package t.jr
package commands

object Irb {

  def apply() = Command.Impl(
    name = "irb_session",
    opts = Map.empty,
  )

}

