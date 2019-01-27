package t.tosti

import t.io.Println

import scala.language.existentials

object Tost {

  import t.fn.pf.{ Pf, Def, Definition, Compose }
  import t.fn.list.{ ::, Nil }
  import t.fn.pfs.{ AppendTo, Select }

  final implicit class TAsserter[T](val unit: Unit) extends AnyVal {
    def apply[V](v: ⇒ V)(implicit ev: V <:< T): Unit = unit
  }

  def tassert[T]: TAsserter[T] = ()

  val li = 0 :: false :: "0" :: Nil
  val li1 = A :: AA :: B :: C :: Nil

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

  // == t.fn.list.pf.Pf ==
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

  // == t.fn.list.pf.Def ==
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
  // == t.fn.list.pf.Compose ==
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
  // == t.fn.list.pf.AppendTo ==
  //
  // (B, A) => A :: B
  ;
  {
    tassert[Int :: Nil] {
      import t.fn.pfs.AppendTo.Decoration
      12.appendTo(Nil)
    }
  }

  //
  // == t.fn.list.pf.Reduce ==
  //
  // Reduces using F
  ;
  {
    tassert[Int :: Boolean :: String :: Nil] {
      import t.fn.reduce.Decoration
      li.reduce(Nil)(AppendTo)
    }
  }


  def main(args: Array[String])(implicit println: Println): Unit = {
    import t.fn.pfs.Select.Decoration
    val lol = B :: AA :: A :: A :: Nil
    val wat = lol.select(F)
    println(lol)
    println(wat)
    println(li1.select(F))
  }
}
