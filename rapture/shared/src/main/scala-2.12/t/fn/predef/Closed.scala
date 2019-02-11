package t.fn
package predef

import scala.language.{higherKinds, implicitConversions}

trait Closed[F[_]] extends Any {

  type Self
  final type Ev = F[Self]
  final type Body[R] = (Ev, Self) ⇒ R

  def ev: Ev
  def self: Self

  final def apply[R](body: Body[R]): R = body(ev, self)

}

object Closed {

  final case class Impl[F[_], T](
    override val self: T
  )(
    implicit
    override val ev: F[T]
  )
    extends AnyRef
      with Closed[F] {
    override type Self = T
  }

  final implicit class Builder[F[_]](val unit: Unit) extends AnyVal {
    def apply[T: F](obj: T): Impl[F, T] = Impl(obj)
  }

  def apply[F[_]]: Builder[F] = ()

  implicit def apply[F[_], T: F](obj: T): Closed[F] = apply[F](obj)

}
