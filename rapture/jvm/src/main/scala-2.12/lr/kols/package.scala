package lr

import scala.collection.generic.{CanBuildFrom, Growable}
import scala.collection.{GenTraversable, GenTraversableLike, IndexedSeqLike, mutable}
import scala.collection.immutable._
import lib.Debuggation

package object kols
  extends AnyRef
{
  trait Contains[Repr] extends Any { type Elem }
  object Contains {
    private[this] case object ContainsInstance extends Contains[Nothing] { type Elem = Nothing }
    implicit def vector[A]: Contains[Vector[A]] { type Elem = A } =
      ContainsInstance.asInstanceOf[Contains[Vector[A]] { type Elem = A }]
    implicit def vectorOpt[A]: Contains[Vector[Option[A]]] { type Elem = Option[A] } =
      ContainsInstance.asInstanceOf[Contains[Vector[Option[A]]] { type Elem = Option[A] }]
  }

  trait Indexable[Repr, A] extends Any {
    def apply(self: Repr, index: Int): A
  }
  object Indexable {
    implicit def vector[A]: Indexable[Vector[A], A] = _(_)
  }

  trait Paddable[Repr, A] extends Any {
    def apply(self: Repr, to: Int, elem: A): Repr
  }
  object Paddable {
    implicit def vector[A]: Paddable[Vector[A], A] = _.padTo(_, _)
  }

  trait Sizable[Repr] extends Any {
    def apply(self: Repr): Int
  }
  object Sizable {
    implicit def vector[A]: Sizable[Vector[A]] = _.size
  }

  trait Updatable[Repr, A] extends Any {
    def apply(self: Repr, index: Int, elem: A): Repr
  }
  object Updatable {
    implicit def vector[A]: Updatable[Vector[A], A] = _.updated(_, _)
  }

  trait Expanding[Repr] extends Any {
    type Elem

    def apply(index: Int)(self: Repr): Option[Elem]

    def update(index: Int)(elem: Elem)(self: Repr): Repr
  }

  final implicit class ExpandingDecoration[Repr](val self: Repr) extends AnyVal {
    type This = ExpandingDecoration[Repr]
    type Ev[A] = Expanding[Repr] { type Elem = A }

    private[this] def ev[A](implicit ev_ : Ev[A]): Ev[A] =
      ev_

    def toExpanding[A: Ev]: This =
      this

    override def toString: String =
      s"Expanding:$self"

    def apply[A: Ev](index: Int): Option[A] =
      ev.apply(index)(self)

    def update[A: Ev](index: Int, elem: A): This =
      ev.update(index)(elem)(self)
  }

  object Expanding {

    final implicit case object Point

    final type Point = Point.type

    final implicit class Expanding_[Repr, A](val point: Point)(
      implicit
      get: Indexable[Repr, Option[A]],
      pad: Paddable[Repr, Option[A]],
      size: Sizable[Repr],
      update_ : Updatable[Repr, Option[A]],
    ) extends Expanding[Repr] {
      type Elem = A

      def expandFor(index: Int)(self: Repr): Repr =
        index match {
          case i if i >= size(self) ⇒ pad(self, i + 1, None)
          case _ ⇒ self
        }

      override def apply(index: Int)(self: Repr): Option[A] =
        get(expandFor(index)(self), index)

      override def update(index: Int)(elem: A)(self: Repr): Repr =
        update_(expandFor(index)(self), index, Some(elem))

    }

    implicit def expanding[Repr, A](
      implicit
      cont: Contains[Repr] { type Elem = Option[A] },
      get: Indexable[Repr, Option[A]],
      pad: Paddable[Repr, Option[A]],
      size: Sizable[Repr],
      update_ : Updatable[Repr, Option[A]],
    ): Expanding[Repr] { type Elem = A } =
      Point
  }

  def lol(): Unit = println {
    Vector(Some(12), None).toExpanding.update(12, 5).tpl(_.apply(11))
      .tpl(_.apply(28))
  }
}

