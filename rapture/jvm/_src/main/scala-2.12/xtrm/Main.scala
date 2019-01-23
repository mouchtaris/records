package xtrm

import xtrm.Main.test

import scala.annotation.tailrec
import scala.collection.generic.CanBuildFrom

object Main {

  object domain extends mse.domain

  object func {
    trait Functor[F[_]] extends Any {
      def apply[A, B](f: A ⇒ B)(fa: F[A]): F[B]
    }
    final implicit class FunctorDecoration[F[_], A](val value: F[A]) extends AnyVal {
      def map[B](f: A ⇒ B)(implicit func: Functor[F]): F[B] =
        func(f)(value)
    }

    trait Bind[F[_]] extends Any {
      def apply[A](value: A): F[A]
    }
    final implicit class BindDecoration[A](val value: A) extends AnyVal {
      def bind[F[_]](implicit b: Bind[F]): F[A] =
        b(value)
    }

    trait Monad[F[_]] extends Any {
      def apply[A, B](f: A ⇒ F[B])(fa: F[A]): F[B]
    }
    final implicit class MonadDecoration[F[_], A](val value: F[A]) extends AnyVal {
      def flatMap[B](f: A ⇒ F[B])(implicit mon: Monad[F]): F[B] =
        mon(f)(value)
      def flatten[AA](implicit ev: A <:< F[AA], mon: Monad[F]): F[AA] =
        flatMap { value ⇒ value }
    }

    implicit def functorFromBindAndMonad[F[_]: Bind: Monad]: Functor[F] =
      new Functor[F] {
        def apply[A, B](f: A ⇒ B)(fa: F[A]): F[B] =
          fa flatMap (f andThen (_.bind[F]))
      }
  }

  object gfun {
    trait Bind[T] extends Any {
      type F[_]
      def apply[T](value: T): F[T]
    }

    trait Monad[T] extends Any {
      this: Any
        with Bind[T]
      ⇒
      def apply[A, B](f: A ⇒ F[B])(fa: F[A]): F[B]
    }
  }

  trait async {
    trait SauceLike[+A] extends Any {
      type F[T] <: SauceLike[T]
      def map[B](f: A ⇒ B): F[B]
      def flatMap[B](f: A ⇒ F[B]): F[B]
    }

    trait SourceLike[+Repr <: SourceLike[Repr, A], +A] extends Any {
      type F[B] <: SourceLike[_, B]
      def map[B](f: A ⇒ B)
    }

    trait SourceApi[+A] extends Any with SourceLike[SourceApi[A], A]
    type Source[A] <: SourceApi[A]
    trait SourceCompanion extends Any
    val Source: SourceCompanion



    trait FlowApi[A, B] extends Any with SourceLike[FlowApi[A, B], B]
    type Flow[A, B] <: FlowApi[A, B]
    trait FlowCompanion extends Any {
      def fromFunction[A, B](f: A ⇒ B): Flow[A, B]
    }
    val Flow: FlowCompanion
    def via[A, B](flow: Flow[A, B])(stream: Source[A]): Source[B]

    trait SourceDecorationApi[A] extends Any {
      def value: Source[A]
      final def via[B](flow: Flow[A, B]): Source[B] =
        async.this.via(flow)(value)
    }
    type SourceDecoration[A] <: SourceDecorationApi[A]
  }

  object test {
    import list._
    import get._
    import list_get._
    import domain._
    import scala.reflect.runtime.universe._

    val email = Email("spong@bob.com")
    val ctype = cred.types.Password
    val cdata = cred.Data("password".getBytes())
    val account = false :: ctype :: 12 :: email :: "Hello" :: cdata :: Unit :: Nil
    type account = Boolean :: cred.Type :: Int :: Email :: String :: cred.Data :: Unit :: Nil

    final case class Account2(
      email: Email,
      ct: cred.Type,
      cd: cred.Data,
    )
    object Account2 {
      implicit val getEmail: Get[Account2, Email] = _.email
      implicit val getCT: Get[Account2, cred.Type] = _.ct
      implicit val getCD: Get[Account2, cred.Data] = _.cd
    }
    val account2 = Account2(email, ctype, cdata)

    final case class Account3(
      email: Email,
      ct: cred.Type,
      cd: cred.Data,
    )
      extends AnyRef
        with (Email :: cred.Type :: cred.Data :: Nil)
    {
      val head: Email = email
      val tail = new (cred.Type :: cred.Data :: Nil) {
        val head: cred.Type = ct
        val tail = new (cred.Data :: Nil) {
          val head: cred.Data = cd
          val tail: Nil = Nil
        }
      }
    }
    val account3 = Account3(email, ctype, cdata)

    def typeOf[T: WeakTypeTag](t: T): Type = implicitly[WeakTypeTag[T]].tpe
    def use_account2[T: domain.Account.e](acc: T): Any = {
      import Account.get
      new String(get[cred.Data](acc).value)
    }
    def test_use_account2(): Unit = {
      println {
        use_account2(account)
      }
      println {
        use_account2(account2)
      }
      println {
        use_account2(account3)
      }
    }

    val async: async = ???
    import async._
    type SignUp = account
    def signUps: Source[SignUp] = ???
    def proc: Flow[SignUp, Int] = ???
    def all: Source[Int] = via(proc)(signUps)
  }

  def main(args: Array[String]): Unit = {
    println("Hi mate")
    test.test_use_account2()
    object handlerion {
      import list._
      import get._
      import list_get._
      import domain._
      import scala.reflect.runtime.universe._





    }

  }

}
