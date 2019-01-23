package mse2

import akka.Done
import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import mse2.Main.{GrandType, TokenType}

import scala.collection.immutable._
import scala.concurrent.{Await, Future}
import scala.concurrent.duration.Duration

object Main {

  trait List extends AnyRef {
    def ::[B <: AnyRef](other: B): other.type :: List.this.type = Cons[other.type, List.this.type](other, this)
  }
  final case class Cons[+A <: AnyRef, +B <: List](head: A, tail: B) extends List
  final type ::[+A <: AnyRef, +B <: List] = A Cons B
  final case object Nil extends List
  final type Nil = Nil.type

  trait Value {
    trait Instance
    type ArgsMagnet[T]
    def apply[T: ArgsMagnet](args: T): Instance
  }

  object ArgMagnet {
  }

  final case class Scalar[T]() extends Value {
    final case class Instance(v: T) extends super[Value].Instance
    override type ArgsMagnet[A] = A <:< T
    override def apply[A](args: A)(implicit mag: ArgsMagnet[A]): Instance =
      Instance(mag(args))
  }

  final case class Struct[L <: List](elems: L) extends Value {
    final case class Instance(v: L) extends super[Value].Instance
    override type ArgsMagnet[A] = A <:< L
    override def apply[A: ArgsMagnet](args: A): Instance =
      Instance(args)
  }

  val Email: Scalar[String] = Scalar[String]()
  val Password: Scalar[String] = Scalar[String]()
  val TokenType: Scalar[Mse.TokenType] = Scalar[Mse.TokenType]()
  val TokenTypeBearer: TokenType.Instance = TokenType(Mse.TokenType.Bearer)
  val GrandType: Scalar[Mse.GrandType] = Scalar[Mse.GrandType]()
  val GrandTypePassword: GrandType.Instance = GrandType(Mse.GrandType.Password)
  val ExpiresIn: Scalar[Long] = Scalar[Long]()
  val CreatedAt: Scalar[Long] = Scalar[Long]()
  val hm0: Nil.type = Nil
  val hm1: Nil = Nil
  val hm2 = Email :: Nil
  //val hm3: Email.type :: Nil = hm2
//  val ResToken: Main.GrandType.type :: Main.Password.type :: Main.ExpiresIn.type :: Main.CreatedAt.type :: Main.Nil.type = GrandType :: Password :: ExpiresIn :: CreatedAt :: Nil

//  trait Make[T] extends Any {
//    type In
//    type Instance
//    def apply(in: In): Instance
//  }
//  object Make {
//    final class Maker[T, in, out](
//      val self: Make[T] { type In = in; type Instance = out }
//    ) extends AnyVal {
//      def by(in: in): out = self(in)
//    }
//    def apply[T](t: T)(implicit make: Make[T]): Maker[T, make.In, make.Instance] = new Maker(make)
//  }
//
//  final class MakeScalar[T](val unit: Unit) extends AnyVal with Make[Scalar[T]] {
//    type In = T
//    type Instance = ScalarInstance[T]
//    def apply(in: In): Instance = ScalarInstance(in)
//  }
//  implicit def makeScalar[T]: MakeScalar[T] = new MakeScalar(())
//
//  final class MakeEnum[E <: Enum](val unit: Unit) extends AnyVal with Make[Enum] {
//    type In = EnumCase[E]
//    type Instance = EnumInstance[E]
//    def apply(in: In): Instance = EnumInstance(in)
//  }
//  implicit def makeEnum[E <: Enum]: MakeEnum[E] = new MakeEnum(())
//
//  final case class MakeList[A, B <: List, Ain, Bin, Ainst, Binst <: List](
//    makeA: Make[A] { type In = Ain; type Instance = Ainst },
//    makeB: Make[B] { type In = Bin; type Instance = Binst },
//  )
//    extends Make[A :: B]
//  {
//    type In = Ain :: Bin
//    type Instance = Ainst :: Binst
//    def apply(in: In): Instance = makeA(in.head) :: makeB(in.tail)
//  }
//
//  implicit def makeList[
//    A, Ain, Ainst,
//    B <: List, Bin, Binst <: List
//  ](
//    implicit
//    makeA: Make[A] { type In = Ain; type Instance = Ainst },
//    makeB: Make[B] { type In = Bin; type Instance = Binst },
//  ): MakeList[A, B, Ain, Bin, Ainst, Binst] =
//    MakeList(makeA, makeB)
//
//  final class MakeNil(val unit: Unit) extends AnyVal with Make[Nil] {
//    type In = Nil
//    type Instance = Nil
//    def apply(in: In): Instance = in
//  }
//
//  implicit def makeNil: MakeNil = new MakeNil(())
//
//  def test_make() = {
//    Make(Email) by "sponge@bob.com"
//  }.toString

  /**
    * Abstracts talking to a Ptrn/Mse API v1.
    */
  object Mse {

    /**
      * Possible token types received
      */
    sealed trait TokenType
    object TokenType {

      object Bearer extends TokenType

    }

    /**
      * Possible grand types requested
      */
    sealed trait GrandType
    object GrandType {

      object Password extends GrandType

    }

    /**
      * Request that can be made
      */
    object req {

      object Token {

        final case class Password(username: String, password: String) {
          val grandType: GrandType = GrandType.Password
        }

      }

    }

    /**
      * Responses that can be received
      */
    object res {

      final case class Token(accessToken: String, tokenType: TokenType, expiresIn: Int, createdAt: Int)

    }

  }

  object Jsonifiers {
    import Json._

    implicit val grandTypeToJson: ToJson[Mse.GrandType] = {
      case Mse.GrandType.Password ⇒
        "password".toJson
    }

    implicit val tokenToJson: ToJson[Mse.req.Token.Password] = {
      token ⇒
        import token._
        Map(
          "grand_type" → grandType.toJson,
          "username" → username.toJson,
          "password" → password.toJson
        ).toJson
    }
  }

  /**
    * The main entry point of mse2.
    *
    * @param args command line argument
    */
  def main(args: Array[String]): Unit = {
    import scala.concurrent.ExecutionContext.Implicits.global

    println("Hello. This is the department of inefficiency.")

    implicit val actorSystem = ActorSystem("LeBobs")
    implicit val actorMaterializer = ActorMaterializer()
    val rest = new Rest()
    import Jsonifiers._
    import Json._

    val json = Seq(
      Json.test(),
      Mse.req.Token.Password("spong@bob.com", "12121212").toJson,
    ).toJson
    val duration = Duration(5, "sec")
    val future = json.toJson
      .reduce(_.concat(_))
      .fold(akka.util.ByteString.empty)(_ ++ _)
      .map(_.utf8String)
      .runForeach(println)
    val done = future.flatMap(_ ⇒ actorSystem.terminate())
      Await.ready(done, duration)
  }
}
