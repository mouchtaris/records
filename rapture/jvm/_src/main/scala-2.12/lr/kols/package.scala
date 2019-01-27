package t.lr

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

  trait Reversible[-T] extends Any {
    type A
    type B
    def apply(self: T): B ⇒ A
  }
  final implicit class ReversibleDecoration[T](val self: T) extends AnyVal {
    def reverse(implicit ev: Reversible[T]): ev.B ⇒ ev.A =
      ev(self)
  }
  object Reversible {
    implicit def map[a, b]: Reversible[Map[a, b]] { type A = a; type B = b } =
      new Reversible[Map[a, b]] {
        override type A = a
        override type B = b
        override def apply(self: Map[a, b]): b ⇒ a =
          self.map { case (a, b) ⇒ (b, a) }
      }
  }

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

    def toVector[A: Ev](implicit size: Sizable[Repr]): Vector[Option[A]] =
      (0 until size(self)).foldLeft(Vector.empty[Option[A]])(_ :+ apply(_))

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

  final case class IndexAdapting[Repr, Ad, I, Ai, Au](
    adapt: Ad,
    back: Repr,
  )(
    implicit
    get: Indexable[Repr, Ai],
    update_ : Updatable[Repr, Au],
    toAdaptor: Ad <:< (I ⇒ Int),
  ) {
    type This = IndexAdapting[Repr, Ad, I, Ai, Au]

    def apply(index: I): Ai =
      get(back, adapt(index))

    def update(index: I, elem: Au): This =
      copy(back = update_(back, adapt(index), elem))

    def keyIndex(implicit rev: Reversible[Ad] { type A = I; type B = Int }): Int ⇒ I =
      rev.apply(adapt)

    override def toString: String =
      s"IndexAdapting[$adapt]:$back"
  }

  object IndexAdapting {

    object Adaptors {

      final case object Identity
        extends (Int ⇒ Int)
      {
        override def apply(i: Int): Int =
          i

        override def toString: String =
          "Identity"

        implicit case object Rev extends Reversible[Identity.type] {
          override type A = Int
          override type B = Int
          override def apply(self: Identity.type): Int ⇒ Int =
            Identity.this
        }
      }

      final case class Vec2(cols: Int)
        extends (((Int, Int)) ⇒ Int)
      {
        override def apply(i: (Int, Int)): Int = i match {
          case (i, j) if j < cols ⇒ (i * cols) + j
          case (i, j) ⇒
            throw new IndexOutOfBoundsException(s"($i, $j): $j probably >= $cols")
        }
        override def toString: String = s"vec2[cols:$cols]"
      }
      object Vec2 {
        implicit def reversible: Reversible[Vec2] { type A = (Int, Int); type B = Int } =
          new Reversible[Vec2] {
            override type A = (Int, Int)
            override type B = Int
            override def apply(v2: Vec2): Int ⇒ (Int, Int) = {
              i ⇒
                val row = i / v2.cols
                val col = i % v2.cols
                (row, col)
            }
          }
      }

      final case class MVec2[a, ada, b, adb](fa: ada, fb: adb, cols: Int)(
        implicit
        toAdaptA: ada <:< (a ⇒ Int),
        toAdaptB: adb <:< (b ⇒ Int),
      )
        extends (((a, b)) ⇒ Int)
      {
        val v2 = Vec2(cols)

        override def apply(i: (a, b)): Int =
          i match {
            case (a, b) ⇒
              v2.apply((fa(a), fb(b)))
          }

        override def toString: String =
          s"mvec2[($fa, $fb)cols:$cols]"
      }

      object MVec2 {
        implicit def reversible[a, ada, b, adb](
          implicit
          reva: Reversible[ada] { type A = a; type B = Int },
          revb: Reversible[adb] { type A = b; type B = Int },
          revv2: Reversible[Vec2] { type A = (Int, Int); type B = Int },
        ): Reversible[MVec2[a, ada, b, adb]] { type A = (a, b); type B = Int } =
          new Reversible[MVec2[a, ada, b, adb]] {
            override type A = (a, b)
            override type B = Int
            override def apply(self: MVec2[a, ada, b, adb]): Int ⇒ (a, b) =
              idx ⇒
                revv2.apply(self.v2).apply(idx) match {
                  case (i, j) ⇒
                    val a = reva.apply(self.fa).apply(i)
                    val b = revb.apply(self.fb).apply(j)
                    (a, b)
                }
          }
      }
    }
  }


  def lol(): Unit = println {
    val back = Vector(Some(12), None)
      .toExpanding
      .update(12, 5)
      .tpl(_.apply(11))
      .tpl(_.apply(28))

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
    val wor = IndexAdapting(IndexAdapting.Adaptors.MVec2(values, values, 5), back)
    Seq(_5, _6, _7, _8)
      .foldLeft(wor) { (c, i) ⇒
        Seq(_1, _2, _3).foldLeft(c) { (c, j) ⇒
          c.update((i, j), 12 - values(i) - values(j))
        }
      }
  }
}

