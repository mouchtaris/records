package t
package tosti

import t.fn.pf.compose.Compose
import io.Println

import scala.language.existentials

object Tost {

  import t.fn.list.appendTo.AppendTo
  import t.fn.list.{::, Nil}
  import t.fn.pf.{Def, Definition, Pf}

  final implicit class TAsserter[T](val unit: Unit) extends AnyVal {
    def apply[V](v: ⇒ V)(implicit ev: V <:< T): Unit = unit
  }

  def tassert[T]: TAsserter[T] = ()

  val li = 0 :: false :: "0" :: Nil
  val li1 = A :: AA :: B :: C :: Nil
  val lia = A :: AA :: Nil

  trait A extends Any
  case object A extends A

  trait AA extends Any with A
  case object AA extends AA

  trait B extends Any
  case object B extends B

  trait C extends Any
  case object C extends C

  trait F extends Any with Def[F]
  object F extends F

  trait G extends Any with Def[G]
  object G extends G

  implicit val pfF: Definition[F, A, B] = Definition(_ ⇒ B)
  implicit val pfG: Definition[G, B, C] = Definition(_ ⇒ C)

  // == Pf ==
  //
  // Is contravariant on F
  ;
  {
    implicitly[Pf[A, Unit] <:< Pf[AA, Unit]]
  }
  //
  // Is contravariant on In
  ;
  {
    implicitly[Pf[Unit, A] <:< Pf[Unit, AA]]
  }
  //
  // Has an apply(In): Out method
  ;
  {
    tassert[F#at[A]#t[B] ⇒ A ⇒ B] {
      f: F#at[A]#t[B] ⇒ f.apply _
    }
  }

  // == Def ==
  //
  // has #at[In]#t[R]
  ;
  {
    implicitly[Def[F]#at[A]#t[B] <:< Pf[F, A] {type Out <: B}]
  }
  //
  // Constructs definitions
  ;
  {
    tassert[Pf[F, A] {type Out <: B}] {
      Def[F, A](_ ⇒ B)
    }
  }

  // == t.fn.list.pf.pf
  //
  // Has a Partial Function Applicator
  ;
  {
    tassert[B] {
      Pf[F](A)
    }
  }

  //
  // == Compose ==
  //
  // Composes functions
  ;
  {
    type Comp = Compose[F, G]
    tassert[C] {
      Pf[Comp](A)
    }
  }

  //
  // == AppendTo ==
  //
  // (B, A) => A :: B
  ;
  {
    tassert[Int :: Nil] {
      import t.fn.list.appendTo.AppendTo.Decoration
      12.appendTo(Nil)
    }
  }

  //
  // == Reduce ==
  //
  // Reduces using F
  ;
  {
    tassert[Int :: Boolean :: String :: Nil] {
      import t.fn.list.reduce.Decoration
      li.reduce(Nil)(AppendTo)
    }
  }

  //
  // == Select ==
  //
  ;
  {
    import t.fn.list.select.Decoration
    tassert[ B :: B :: Nil ] {
      li1.select(F)
    }
  }

  //
  // == Length ==
  //
  ;
  {
    import t.fn.list.length.Length
    import t.fn.nat._
    tassert[ _0 ] { Pf[Length](Nil) }
    tassert[ _1 ] { Pf[Length](A :: Nil) }
    tassert[ _2 ] { Pf[Length](A :: B :: Nil) }
  }

  //
  // == Map ==
  //
  ;
  {
    import t.fn.list.map.Map.{ Decoration ⇒ __MD }
    tassert[ B :: B :: Nil ] {
      lia.map(F)
    }
    import t.fn.list.select.{ Decoration ⇒ __SD }
    li1.select(F)
  }


  def main(args: Array[String])(implicit println: Println): Unit = {
  }
}
