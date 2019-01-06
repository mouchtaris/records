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

    kols.lol()
  }

}
