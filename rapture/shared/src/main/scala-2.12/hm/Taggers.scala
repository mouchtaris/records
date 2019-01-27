package hm

object Taggers {

  trait String extends Tagger[Predef.String]
  trait Int extends Tagger[scala.Int]

}
