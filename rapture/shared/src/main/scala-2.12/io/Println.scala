package io

trait Println extends Any {

  def apply(): Unit
  def apply(str: String): Unit
  def apply(obj: Any): Unit

}

object Println {

  class PrintStreamPrintln(val stream: java.io.PrintStream)
    extends AnyVal
    with Println
  {
    override def apply(): Unit = stream.println()
    override def apply(str: String): Unit = stream.println(str)
    override def apply(obj: Any): Unit = stream.println(obj)
  }

  object Implicits {

    implicit val stdout: PrintStreamPrintln = new PrintStreamPrintln(Console.out)

  }

}
