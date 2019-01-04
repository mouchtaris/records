package lr

object Main {

  val g = Generation(Grammars.Example)

  def main(args: Array[String]): Unit = {
    println("Welcome to Parsing Meletalathron")
    println("== Grammar ==")
    println(Grammars.Example.start)
    println(Grammars.Example)
    println("== States ==")
    val s0 = g.gstate0
    println(s0)
    println("==")
    val s1 = g next s0
    println(s1)
    println("==")
    val s2 = g next s1
    println(s2)
  }

}
