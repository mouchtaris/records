package lr

import scala.collection.generic.{CanBuildFrom, Growable}
import scala.collection.{GenTraversable, GenTraversableLike, IndexedSeqLike, mutable}
import scala.collection.immutable._
import lib.Debuggation

package object kols
  extends AnyRef {

  trait Contains[Repr] extends Any {
    type Elem
  }

  object Contains {

    private[this] case object ContainsInstance extends Contains[Nothing] {
      type Elem = Nothing
    }

    implicit def vector[A]: Contains[Vector[A]] {type Elem = A} =
      ContainsInstance.asInstanceOf[Contains[Vector[A]] {type Elem = A}]

    implicit def vectorOpt[A]: Contains[Vector[Option[A]]] {type Elem = Option[A]} =
      ContainsInstance.asInstanceOf[Contains[Vector[Option[A]]] {type Elem = Option[A]}]

    final implicit class `Bind A`[A](val unit: Unit) extends AnyVal {
      def apply[Repr](): Contains[Repr] {type Elem = A} =
        ContainsInstance.asInstanceOf[Contains[Repr] {type Elem = A}]
    }

    def apply[A]: `Bind A`[A] = ()

    def apply[Repr, A](): Contains[Repr] {type Elem = A} = apply[A]()

    final implicit class `Bind Repr`[Repr](val unit: Unit) extends AnyVal {
      def apply[A: ContainedIn[Repr]#t](): Contains[Repr] {type Elem = A} = Contains()
    }

    def wat[Repr]: `Bind Repr`[Repr] = ()
  }

  type ContainedIn[Repr] = {type t[A] = Contains[Repr] {type Elem = A}}

  trait Indexable[Repr, A] extends Any {
    def apply(self: Repr, index: Int): A
  }

  object Indexable {
    implicit def vector[A]: Indexable[Vector[A], A] = _ (_)
  }

  type IndexableIn[Repr] = {type t[A] = Indexable[Repr, A]}

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

  type UpdatableIn[Repr] = {type t[A] = Updatable[Repr, A]}

  trait Expanding[Repr] extends Any {
    type Elem

    def apply(index: Int)(self: Repr): Option[Elem]

    def update(index: Int)(elem: Elem)(self: Repr): Repr
  }

  final implicit class ExpandingDecoration[Repr](val self: Repr) extends AnyVal {
    type This = ExpandingDecoration[Repr]
    type Ev[A] = Expanding[Repr] {type Elem = A}

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

  object ExpandingDecoration {
    implicit def contains[
    Repr,
    A: ContainedIn[Repr]#t
    ]: Contains[ExpandingDecoration[Repr]] {type Elem = Option[A]} =
      Contains()

    implicit def indexable[Repr, A](
      implicit
      ev: Expanding[Repr] {type Elem = A}
    ): Indexable[ExpandingDecoration[Repr], Option[A]] = _ apply _

    implicit def updatable[Repr, A](
      implicit
      ev: Expanding[Repr] {type Elem = A}
    ): Updatable[ExpandingDecoration[Repr], A] = _ update(_, _)
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
      cont: Contains[Repr] {type Elem = Option[A]},
      get: Indexable[Repr, Option[A]],
      pad: Paddable[Repr, Option[A]],
      size: Sizable[Repr],
      update_ : Updatable[Repr, Option[A]],
    ): Expanding[Repr] {type Elem = A} =
      Point
  }

  final case class IndexAdapting[Repr, I, Ai, Au](
    adapt: I ⇒ Int,
    back: Repr,
  )(
    implicit
    get: Indexable[Repr, Ai],
    update_ : Updatable[Repr, Au],
  ) {
    type This = IndexAdapting[Repr, I, Ai, Au]

    def apply(index: I): Ai =
      get(back, adapt(index))

    def update(index: I, elem: Au): This =
      copy(back = update_(back, adapt(index), elem))

    override def toString: String =
      s"IndexAdapting[$adapt]:$back"
  }

  object IndexAdapting {

    object Adaptors {

      def vec2(cols: Int): ((Int, Int)) ⇒ Int = new (((Int, Int)) ⇒ Int) {
        override def apply(i: (Int, Int)): Int = i match {
          case (i, j) if j < cols ⇒ (i * cols) + j
        }
        override def toString: String = s"vec2[cols:$cols]"
      }

      def mvec2[A, B](fa: A ⇒ Int, fb: B ⇒ Int, cols: Int): ((A, B)) ⇒ Int =  new (((A, B)) ⇒ Int) {
        private[this] val v2 = vec2(cols)

        override def apply(i: (A, B)): Int = i match {
          case (a, b) ⇒ v2((fa(a), fb(b)))
        }

        override def toString: String = s"mvec2[($fa, $fb)cols:$cols]"
      }
    }

    final class Ctor[I](val adapt: I ⇒ Int) extends AnyVal {
      def apply[Repr, Ai: IndexableIn[Repr]#t, Au: UpdatableIn[Repr]#t](back: Repr) = IndexAdapting[Repr, I, Ai, Au](
        adapt, back
      )
    }

    def vec2(cols: Int) = new Ctor(Adaptors.vec2(cols))
    def mvec2[A, B](fa: A ⇒ Int, fb: B ⇒ Int, cols: Int) = new Ctor(Adaptors.mvec2(fa, fb, cols))
  }


  def lol(): Unit = println {
    val back = Vector(Some(12), None)
      .toExpanding
      .update(12, 5)
      .tpl(_.apply(11))
      .tpl(_.apply(28))

    val wat = IndexAdapting.vec2(5)
    (5 to 8).foldLeft(wat(back)) { (c, i) ⇒ (1 to 3).foldLeft(c) { (c, j) ⇒ c.update((i, j),  12 - i -j) } }

    sealed trait N extends Any
    final implicit case object _1 extends N
    final implicit case object _2 extends N
    final implicit case object _3 extends N
    final implicit case object _4 extends N
    final implicit case object _5 extends N
    final implicit case object _6 extends N
    final implicit case object _7 extends N
    final implicit case object _8 extends N
    final implicit case object _9 extends N
    val values = Map(_1 → 1, _2 → 2, _3 → 3, _4 → 4, _5 → 5, _6 → 6, _7 → 7, _8 → 8, _9 → 9)
    val wor = IndexAdapting.mvec2(values, values, 5)
    Seq(_5, _6, _7, _8)
      .foldLeft(wor(back)) { (c, i) ⇒
        Seq(_1, _2, _3).foldLeft(c) { (c, j) ⇒
          c.update((i, j), 12 - values(i) - values(j))
        }
      }
  }
}

