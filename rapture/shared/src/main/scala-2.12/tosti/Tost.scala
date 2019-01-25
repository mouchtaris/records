package tosti

import scala.language.existentials

object Tost {

  import fn.list.{::, Nil}
  import fn.list.appendTo.AppendTo
  import fn.list.pf.{pf, Compose, Def, Pf}
  import fn.list.reduce.ReduceDecoration
  import fn.list.reduce.implicits._

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

  implicit val pfF: Def.Definition[F, A, B] = Def.Definition(_ ⇒ new B {})
  implicit val pfG: Def.Definition[G, B, C] = Def.Definition(_ ⇒ new C {})

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
      Def.Definition[F, A, B](_ ⇒ new B {})
    }
  }

  // == fn.list.pf.pf
  //
  // Has a Partial Function Applicator
  ;
  {
    tassert[B] {
      pf[F](new A {})
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
      pf[Comp](new A {})
    }
  }

  //
  // == fn.list.pf.AppendTo ==
  //
  // (B, A) => A :: B
  ;
  {
    tassert[Int :: Nil] {
      pf[AppendTo]((Nil, 12))
    }
  }

  //
  // == fn.list.pf.Reduce ==
  //
  // Reduces using F
  ;
  {
    tassert[Int :: Boolean :: String :: Nil] {
      li.reduce(Nil, AppendTo)
    }
  }


  def main(args: Array[String]): Unit = {
    import fn.list.select.SelectDecoration
    val lol = new B {} :: new AA {} :: new A {} :: new A{} :: Nil
    val wat = lol select F
  }
}
