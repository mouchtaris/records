package tosti

import scala.language.existentials

object Tost {

  import fn.pf.{ Pf, Def, Definition, Compose }
  import fn.list.{ ::, Nil }
  import fn.pfs.AppendTo

  final implicit class TAsserter[T](val unit: Unit) extends AnyVal {
    def apply[V](v: ⇒ V)(implicit ev: V <:< T): Unit = unit
  }

  def tassert[T]: TAsserter[T] = ()

  val li = 0 :: false :: "0" :: Nil
  val li1 = new A {} :: new AA {} :: new B {} :: new C {} :: Nil

  trait A extends Any

  trait AA extends Any with A

  trait B extends Any

  trait C extends Any

  trait F extends Any with Def[F]

  object F extends F

  trait G extends Any with Def[G]

  object G extends G

  implicit val pfF: Definition[F, A, B] = Definition(_ ⇒ new B {})
  implicit val pfG: Definition[G, B, C] = Definition(_ ⇒ new C {})

  // == fn.list.pf.Pf ==
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

  // == fn.list.pf.Def ==
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
      Def[F, A](_ ⇒ new B {})
    }
  }

  // == fn.list.pf.pf
  //
  // Has a Partial Function Applicator
  ;
  {
    tassert[B] {
      Pf[F](new A {})
    }
  }

  //
  // == fn.list.pf.Compose ==
  //
  // Composes functions
  ;
  {
    type Comp = Compose[F, G]
    tassert[C] {
      Pf[Comp](new A {})
    }
  }

  //
  // == fn.list.pf.AppendTo ==
  //
  // (B, A) => A :: B
  ;
  {
    tassert[Int :: Nil] {
      import fn.pfs.AppendTo.Decoration
      12.appendTo(Nil)
    }
  }

  //
  // == fn.list.pf.Reduce ==
  //
  // Reduces using F
  ;
  {
    tassert[Int :: Boolean :: String :: Nil] {
      import fn.reduce.Decoration
      li.reduce(Nil)(AppendTo)
    }
  }


  def main(args: Array[String]): Unit = {
    import fn.pfs.Select.Decoration
    val lol = new B {} :: new AA {} :: new A {} :: new A{} :: Nil
    val wat = lol.select(F)
  }
}
