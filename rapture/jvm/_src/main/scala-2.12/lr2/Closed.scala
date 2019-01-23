package lr2

trait Closed[TC[a] <: TypeClass[a]] extends Any {

  type ?

  def obj: ?
  def ev: TC[?]

  final def r[V](f: TC[?] ⇒ TC[?]#R[V]): V =
    f(ev)(obj)

}

object Closed {

  final case class Impl[TC[a] <: TypeClass[a], T](
    override val obj: T,
    override val ev: TC[T],
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