package panda
import scala.util.{
  Try,
  Success,
  Failure,
}

trait Magnet[P, T] extends Any {
  def apply(pole: P): T
}

object Magnet {

  type ized[T] = {
    type from[P] = P Magnet T
  }

  final class Magneto[T](val unit: Unit) extends AnyVal {
    def apply[P](pole: P)(implicit ev: P Magnet T): T =
      ev(pole)
  }

  def apply[T]: Magneto[T] = new Magneto(())

  implicit def selfMganetization[T]: T Magnet T =
    identity[T]

  implicit def selfOptionMagnetization[T]: T Magnet Option[T] =
    Some(_)

  implicit def selfTryMagnetization[T]: T Magnet Try[T] =
    Success(_)

  implicit def conjuicedOptionMagnetization[A, B](implicit ev: A Magnet B): A Magnet Option[B] =
    a ⇒ Some(ev(a))

  implicit def conjuicedTryMagnetization[A, B](implicit ev: A Magnet B): A Magnet Try[B] =
    a ⇒ Success(ev(a))

}
