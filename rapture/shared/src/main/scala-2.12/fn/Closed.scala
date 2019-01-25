package fn

import scala.language.higherKinds

trait Closed[TC[a] <: TypeClass[a]] extends Any {

  type ?

  final type typeClass = TC[?]

  def typeClass: typeClass
  def self: ?

  final def r[V](f: typeClass ⇒ typeClass#R[V]): V =
    f(typeClass)(self)

}

object Closed {

  final case class Impl[TC[a] <: TypeClass[a], T](
    override val self: T,
    override val typeClass: TC[T],
  )
    extends AnyRef
      with Closed[TC]
  {
    type ? = T
  }

  final class Closer[F[a] <: TypeClass[a]](val unit: Unit) extends AnyVal {
    def apply[T](obj: T)(implicit ev: F[T]): Impl[F, T] = Impl(obj, ev)
  }

  def apply[F[a] <: TypeClass[a]]: Closer[F] = new Closer[F](())

}
