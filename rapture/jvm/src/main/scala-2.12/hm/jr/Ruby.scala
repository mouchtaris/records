package hm
package jr

object Ruby {

  def main(args: Array[String]): Unit = {
    import t.jr.Hatchy.{ Ruby ⇒ HRuby, _ }

    val ruby = HRuby(
      gemEnv = rc / "_envs",
      lib = rc
    )
    val engine = org.jruby.Ruby.newInstance()
    def exec(comm: Command): Unit = {
      val code: String = ruby.toCode(comm)
      val lined = code
          .lines
          .zipWithIndex
          .map { case (line, i) ⇒ "%03d:  %s".format(i, line) }
          .mkString("\n")
      println(s"*** Executing:\n$lined")
      engine.runFromMain(code, "")
    }


//    exec(ruby.install_gem("bundler", Some("1.17.3")))
    exec(ruby.irb)
  }


}
