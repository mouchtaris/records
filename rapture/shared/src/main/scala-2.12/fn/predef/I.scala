package fn
package predef

object I {
  final type I[T] = T

  def apply[T <: AnyRef](implicit t: T): t.type = t
}
