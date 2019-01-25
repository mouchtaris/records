package fn
import predef.Certainly

import scala.annotation.implicitNotFound
import scala.language.higherKinds

object list {

  trait List extends Any

  trait ::[+A, +B <: List] extends Any with List {
    def head: A

    def tail: B

    final override def toString: String =
      s"$head :: $tail"
  }

  object :: {
    def unapply[A, B <: List](list: A :: B): Certainly[(A, B)] = (list.head, list.tail)
  }

  final implicit case object Nil extends List {
    override def toString: Predef.String =
      "Nil"
  }

  final type Nil = Nil.type

  final implicit class ListImplOps[T <: List](val value: T) extends AnyVal {
    def ::[h](h: h)(implicit dummyImplicit: DummyImplicit): h :: T = Cons(h, value)
  }

  final case class Cons[+A, +B <: List](head: A, tail: B)
    extends AnyRef
      with (A :: B)

  object pf {

    @implicitNotFound(msg = "Pf ${F} not defined at ${In}")
    trait Pf[-F, -In] extends Any {
      type Out

      def apply(in: In): Out
    }

    trait Def[F] extends Any {

      sealed trait at[In] extends Any {
        type t[R] = Pf[F, In] {type Out = R}
      }

    }

    object Def {

      final implicit class Definition[F, In, Out_](val self: In ⇒ Out_)
        extends AnyVal
          with Pf[F, In] {
        override type Out = Out_

        override def apply(in: In): Out = self(in)
      }

    }

    final class Applicator[F](val unit: Unit) extends AnyVal {
      def apply[A](a: A)(implicit f: F Pf A): f.Out =
        f(a)
    }

    def pf[F]: Applicator[F] = new Applicator(())


    sealed trait Compose[F, G] extends Any

    final class ComposePf[F, G, In, A: Def[F]#at[In]#t, B: Def[G]#at[A]#t]
      extends AnyRef
        with Pf[F Compose G, In] {
      override type Out = B

      override def apply(in: In): B = pf[G](pf[F](in))
    }

    sealed trait ComposePriorityLow extends Any

    sealed trait ComposePriorityHigh extends Any {
      implicit def compose[
      F,
      G,
      In,
      A: Def[F]#at[In]#t,
      B: Def[G]#at[A]#t,
      ]: ComposePf[F, G, In, A, B] =
        new ComposePf
    }

    object Compose
      extends AnyRef
        with ComposePriorityHigh {

      final implicit class apply[F, G](val unit: Unit = ()) extends AnyVal with Compose[F, G]

    }


  }

  object reduce {

    import
    pf.{
      Pf,
      Def,
    }

    sealed trait Reduce[Zero, F] extends Any with Def[Reduce[Zero, F]]

    final implicit class ReduceNil[Zero, F](val unit: Unit)
      extends AnyVal
        with Pf[Reduce[Zero, F], (Zero, Nil)] {
      override type Out = Zero

      override def apply(in: (Zero, Nil)): Zero = in match {
        case (zero, _) ⇒ zero
      }
    }

    final class ReduceList[Zero, F, H, T <: List, R, RT](
      implicit
      reduce: Def[Reduce[Zero, F]]#at[(Zero, T)]#t[RT],
      f: Def[F]#at[(RT, H)]#t[R],
    )
      extends AnyRef
        with Pf[Reduce[Zero, F], (Zero, H :: T)] {
      override type Out = R

      override def apply(in: (Zero, H :: T)): R = in match {
        case (zero, head :: tail) ⇒ f((reduce((zero, tail)), head))
      }
    }

    trait ReduceLowPriority extends Any

    trait ReduceHighPriority extends Any with ReduceLowPriority {
      implicit def reduceList[
      Zero,
      F,
      H,
      T <: List,
      RT: Def[Reduce[Zero, F]]#at[(Zero, T)]#t,
      R: Def[F]#at[(RT, H)]#t,
      ]: ReduceList[Zero, F, H, T, R, RT] =
        new ReduceList

      implicit def reduceNil[Zero, F]: ReduceNil[Zero, F] = ()
    }

    final implicit class ReduceDecoration[L <: List](val self: L) extends AnyVal {
      def reduce[F, Zero](zero: Zero, f: F)(implicit red: Pf[Reduce[Zero, F], (Zero, L)]): red.Out =
        red((zero, self))
    }

    object implicits extends AnyRef with ReduceHighPriority

  }

  object appendTo {

    import pf.Def

    trait AppendTo extends Any with Def[AppendTo]

    object AppendTo extends AnyRef with AppendTo

    implicit def appendToDefined[H, T <: List]: Def.Definition[AppendTo, (T, H), H :: T] = Def.Definition {
      case (tail, head) ⇒ head :: tail
    }
  }

  object select {
    import pf.{ Pf, Def, pf ⇒ pfapp }
    import reduce.Reduce

    trait SelectReducer[F] extends Any with Def[SelectReducer[F]]
    final implicit class SelectReducerOnUndefined[F, Zero, In](val unit: Unit)
      extends AnyVal
      with Pf[SelectReducer[F], (Zero, In)] {
      override type Out = Zero

      override def apply(comb: (Zero, In)): Out = comb match {
        case (zero, _) ⇒ zero
      }
    }
    final class SelectReducerOnDefined[F, Zero <: List, In, R: Def[F]#at[In]#t]
      extends AnyRef
      with Pf[SelectReducer[F], (Zero, In)] {
      override type Out = R :: Zero

      override def apply(comb: (Zero, In)): Out = comb match {
        case (zero, in) ⇒ pfapp[F](in) :: zero
      }
    }
    sealed trait SelectReducerLow extends Any {
      implicit def selectReducerOnUndefined[F, Zero, In]: SelectReducerOnUndefined[F, Zero, In] = ()
    }
    sealed trait SelectReducerHigh extends Any {
      implicit def selectReducerOnDefined[
        F, Zero <: List, In, R: Def[F]#at[In]#t
      ]: SelectReducerOnDefined[F, Zero, In, R] =
        new SelectReducerOnDefined
    }
    object SelectReducer extends AnyRef with SelectReducerHigh {
      final implicit class apply[F](val unit: Unit) extends AnyVal with SelectReducer[F]
    }
    type Select[F] = Reduce[Nil, SelectReducer[F]]
    final implicit class SelectDecoration[S](val self: S) extends AnyVal {
      def select[F](f: F)(implicit ev: Pf[Select[F], (Nil, S)]): ev.Out = ev((Nil, self))
    }
  }

}
