package hm
package jrubies

trait CommandLike extends Any {
  def args: Seq[String]

  final def +(moreArgs: Seq[String])= new Command(args ++ moreArgs)
  final def +(moreArg: String) = new Command(args :+ moreArg)
  final def apply(): Unit = {
    println(s"Running: ${args.mkString(" ")}")
    org.jruby.Main.main(args.toArray)
  }
}