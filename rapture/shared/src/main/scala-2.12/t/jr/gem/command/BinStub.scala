package t.jr
package gem.command

object BinStub {

  def apply(gemName: String, bin: String, args: Vector[String]) = Command.Impl(
    name = "gem_bin_stub",
    opts = Map(
      params.gemName → gemName,
      params.bin → bin,
      params.argv → args,
    )
  )

}
