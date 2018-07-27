package hm

object Incubate {

  object closed_type_classes {

    trait TypeClass extends Any

    trait Sample[T] extends Any with TypeClass {
      def op(self: T)(arg: Int): String
    }

    implicit object IntSample extends Sample[Int] {
      def op(self: Int)(arg: Int): String = (self + arg).toString
    }

    trait Closed[TC[_] <: TypeClass] {
      type T
      def self: T
      def tc: TC[T]

      final def apply[A, B](f: TC[T] ⇒ T ⇒ A ⇒ B): A ⇒ B = f(tc)(self)
    }

    object Closed {
      final class Ctr[TC[_] <: TypeClass](val value: Unit) extends AnyVal {
        def apply[_T](_self: _T)(implicit _tc: TC[_T]): Closed[TC] =
          new Closed[TC] {
            type T = _T
            def self: T = _self
            def tc: TC[T] = _tc
          }
      }
      def apply[TC[_] <: TypeClass]: Ctr[TC] =
        new Ctr[TC](())
    }

    val closedSample: Closed[Sample] = Closed[Sample](12)
    def test(): Unit = {
      println {
        closedSample(_.op)(23)
      }
    }
  }

  object duck {
    import closed_type_classes.{
      TypeClass,
      Closed,
    }

    sealed trait Type extends scala.Any
    object Type {
      final case class Placeholder() extends Type
      def apply(): Placeholder = Placeholder()
    }

    final case class Any() extends Type
    final case class Unit() extends Type
    final case class Effect(t: Type) extends Type
    final case class ==>(arg: Type, result: Type) extends Type
    final case class Object(ops: Map[String, Type]) extends Type

    //
    // def f a b = a + b
    val f_t = {
      val b_t = Any()
      val result_t = Type()
      val plus_t = ==>(b_t, result_t)
      val a_t = Object(Map(
        "+" → plus_t
      ))
      ==>(a_t, plus_t)
    }

    final case class F[A]()
    def map[A, B]       : F[A]  ⇒ (A ⇒ B)     ⇒ F[B]  = fa ⇒ f ⇒ flatMap(fa)(f andThen bind)
    def bind[A]         : A     ⇒ F[A]                = ???
    def flatMap[A, B]   : F[A]  ⇒ (A ⇒ F[B])  ⇒ F[B]  = ???
    def app[A, B]       : F[A]  ⇒ F[A ⇒ F[B]] ⇒ F[B]  = ???

    trait Functor[F[_]] {
      def apply[A, B](fa: F[A], f: A ⇒ B): F[B]
    }
    trait Bind[F[_]] {
      def apply[A](a: A): F[A]
    }
    trait Monad[F[_]] {
      def apply[A, B](fa: F[A], f: A ⇒ F[B]): F[B]
    }
    implicit object BindF extends Bind[F] {
      def apply[A](a: A): F[A] = F[A]()
    }
    implicit object MonadF extends Monad[F] {
      def apply[A, B](fa: F[A], f: A ⇒ F[B]): F[B] = F[B]()
    }
    implicit class FunctorDecoration[F[_], A](val value: F[A]) extends AnyVal {
      def map[B](f: A ⇒ B)(implicit func: Functor[F]): F[B] = func(value, f)
    }
    implicit class BindDecoration[A](val value: A) extends AnyVal {
      def bind[F[_]](implicit b: Bind[F]): F[A] = b(value)
    }
    implicit class MonadDecoration[F[_], A](val value: F[A]) extends AnyVal {
      def flatMap[B](f: A ⇒ F[B])(implicit monad: Monad[F]): F[B] = monad(value, f)
    }
    implicit def `monad + bind ⇒ functor`[F[_]](implicit monad: Monad[F], bind: Bind[F]): Functor[F] =
      new Functor[F] {
        def apply[A, B](fa: F[A], f: A ⇒ B): F[B] =
          monad(fa, (a: A) ⇒ bind(f(a)))
      }


    val fa = F[Int]()
    val fb = fa map (_ + 1)
    val fc = fa flatMap bind
  }


}
