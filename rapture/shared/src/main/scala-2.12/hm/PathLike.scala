package hm

trait PathLike[Repr] extends Any {
  import PathLike.Elements

  final type Ctr = Elements ⇒ Repr

  def elements: Elements

  final def from[T <: PathLike.Any](base: T)(implicit c: Ctr): Repr = c(base.elements ++ elements)
  final def /[T <: PathLike.Any](other: T)(implicit c: Ctr): Repr = c(elements ++ other.elements)
  final def /(other: String)(implicit c: Ctr): Repr = c(elements :+ other)
  final def path: String = elements mkString "/"
}

object PathLike {
  final type Any = PathLike[_]
  final type Elements = Vector[String]
}
