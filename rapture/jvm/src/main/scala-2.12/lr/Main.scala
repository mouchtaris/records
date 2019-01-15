package lr

object Main {
  def main(args: Array[String]): Unit = {
    for {
      g ← Seq(Grammars.Example, Grammars.Example2)
    }
      new Main(Generation(g)).main(args)
  }

  class Main(
    val g: Generation = Generation(Grammars.Example),
  ) {

    def main(args: Array[String]): Unit = {
      println("\n\n\n\n ****** Welcome to Parsing Meletalathron")
      println("== Grammar ==")
      println(g.grammar.start)
      println(g.grammar)
      println("== States ==")
      val s0 = g.gstate0
      println(s0)
      println("==")
      val s1 = g next s0
      println(s1)
      println("==")
      val s2 = g next s1
      println(s2)
      println("==")
      val s3 = g next s2
      println(s3)
      println("==")
      val s4 = g next s3
      println(s4)
      println("==")
      val s5 = g next s4
      println(s5)
      println("==")
      val s6 = g next s5
      println(s6)

      Seq(
        g,
      )
        .foreach { g ⇒
          println {
            g.First.Tests.runReport(g.First.tests)
          }
          println(g.grammar)
          g.grammar.symbolTable.keys
            .map {
              s ⇒
                (
                  s,
                  g.First(s),
                  g.Follow(s),
                )
            }.foreach {
            case (s, first, follow) ⇒
              println(s"FIRST($s) = $first | FOLLOW($s) = $follow")
          }
        }
    }
  }

}
