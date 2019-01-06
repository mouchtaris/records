package lr

import scala.collection.generic.{CanBuildFrom, GenericCompanion, GenericTraversableTemplate, IndexedSeqFactory}
import scala.collection.{GenSeqLike, GenTraversableLike, IndexedSeqLike, IndexedSeqOptimized, SeqLike, mutable}
import scala.collection.immutable._

trait SizedPkg extends Any {
  trait Sized[-T] extends Any { def apply(self: T): Int }
  trait SizedDecoration[T] extends Any { def size: Int }
  implicit def sizedDecoration[T](obj: T)(implicit ev: Sized[T]): SizedDecoration[T] = new SizedDecoration[T] {
    def size: Int = ev(obj)
  }
}

trait SizedProviders extends Any {
  this: SizedPkg ⇒

  implicit def sizedGenTraversableLike[T, Repr]: Sized[GenTraversableLike[T, Repr]] = _.size
}

trait ContainingPkg extends Any {
  final class Containing[-C, T]

  sealed trait Container[C] extends Any {
    type of[T] = Containing[C, T]
  }
}

trait ContainingProviders extends Any {
  this: ContainingPkg ⇒

  implicit def containingGenTraversableLike[T, Repr]: Containing[GenTraversableLike[T, Repr], T] = new Containing()
}

package object kols extends AnyRef
  with SizedPkg
  with SizedProviders
  with ContainingPkg
  with ContainingProviders {

  final type ExpandingFacadeWithRepr[Repr] = {
    type t[A] = ExpandingFacade[A, Repr]
  }

  final implicit class ExpandingFacade[
  A,
  Repr <: IndexedSeq[A],
  ](
    val self: Repr
  )
    extends AnyRef
      with IndexedSeq[A]
      with GenericTraversableTemplate[A, ExpandingFacadeWithRepr[Repr]#t]
      with IndexedSeqLike[A, ExpandingFacadeWithRepr[Repr]#t[A]] {
    override def length: Int = self.length

    override def apply(idx: Int): A = self.apply(idx)

    override def companion: GenericCompanion[ExpandingFacadeWithRepr[Repr]#t] =
      new GenericCompanion[ExpandingFacadeWithRepr[Repr]#t] {
        override def newBuilder[AA]: mutable.Builder[AA, ExpandingFacade[AA, Repr]] = ???
      }
  }

  object ExpandingFacade {

    implicit def canBuildFrom[Repr, A, B](
      implicit
      cbf: CanBuildFrom[Repr, B, Repr]
    ): CanBuildFrom[ExpandingFacade[A, Repr], B, ExpandingFacade[B, Repr]] =
      new CanBuildFrom[ExpandingFacade[A, Repr], B, ExpandingFacade[B, Repr]] {
      }
  }

  def lol = {
    val e = new ExpandingFacade(Vector(12))
    val wot: ExpandingFacade[Vector[Int], Int] = e.updated(23, 34)
  }
}

