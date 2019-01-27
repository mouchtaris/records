package hatch

object Main {

  def main(args: Array[String]): Unit = {
    import t.io.Println.Implicits.stdout
    t.tosti.Tost.main(args)
  }

}
