package lr

object Main {

  val g = Generation(Grammars.Example)

  def main(args: Array[String]): Unit = {
    println("Welcome to Parsing Meletalathron")
    println("== Grammar ==")
    println(Grammars.Example.start)
    println(Grammars.Example)
    println("== States ==")
    println(g.gstate0)
    println("==")
    println(g.next(g.gstate0))
  }

}
