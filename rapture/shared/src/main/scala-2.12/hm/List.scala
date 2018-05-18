package hm

sealed trait List extends AnyRef {
  def ::[h](h: h): List.::[h, this.type] =
    List.::(h, this)
}

object List {

  final case class ::[h, t <: List](head: h, tail: t) extends List {
    override def toString: String =
      s"$head :: $tail"
  }

  final case object Nil extends List
  final type Nil = Nil.type

}



