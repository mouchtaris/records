package xtrm

object tag {
  trait Tag extends Any

  final class Tagged[T <: Tag, V](val value: V) extends AnyVal

  final class Tagger[T <: Tag](val unit: Unit) extends AnyVal {
    def apply[V](value: V) = new Tagged[T, V](value)
  }

  def apply[T <: Tag] = new Tagger[T](())

  trait TaggingContext extends Any {
    sealed trait Tag extends tag.Tag
  }

  trait SpecificTaggingContext[V]
    extends Any
      with TaggingContext
  {
    final type Tagged = tag.Tagged[Tag, V]
    def apply(value: V): Tagged = tag[Tag](value)
  }
}
