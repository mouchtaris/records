package xtrm

object domain {
  import list._
  import get._
  import tag._
  import model._

  trait New[V] extends Any with SpecificTaggingContext[V]

  final object credential {
    sealed trait typ
    object typ {
      val get = Get[typ]
    }
    final object types {
      final case object Password extends typ
    }

    final case object data extends New[Array[Byte]] {
      val get = Get[data]
    }
    final type data = data.Tagged
  }

  final object email extends New[String] {
    val get = Get[email]
  }
  final type email = email.Tagged

  type account = email :: credential.typ :: credential.data :: Nil
  val account = Record[account]
}

object test {
  import list._
  import get._
  import list_get._
  import domain._
  import scala.reflect.runtime.universe._

  def email = domain.email("spong@bob.com")
  def ctype = domain.credential.types.Password
  def cdata = credential.data("password".getBytes())
  def account = false :: ctype :: 12 :: email :: "Hello" :: cdata :: Unit :: Nil
  type account = Boolean :: credential.typ :: Int :: email :: String :: credential.data :: Unit :: Nil

  final case class Account2(
    email: domain.email,
    ct: domain.credential.typ,
    cd: domain.credential.data,
  )
  object Account2 {
    implicit val getEmail: Get[Account2, domain.email] = _.email
    implicit val getCT: Get[Account2, domain.credential.typ] = _.ct
    implicit val getCD: Get[Account2, domain.credential.data] = _.cd
  }
  val account2 = Account2(email, ctype, cdata)

  final case class Account3(
    email: domain.email,
    ct: domain.credential.typ,
    cd: domain.credential.data,
  )
    extends AnyRef
    with (domain.email :: domain.credential.typ :: domain.credential.data :: Nil)
  {
    def head: email = email
    def tail = new (domain.credential.typ :: domain.credential.data :: Nil) {
      def head: credential.typ = ct
      def tail = new (domain.credential.data :: Nil) {
        def head: domain.credential.data = cd
        def tail: Nil = Nil
      }
    }
  }
  val account3 = Account3(email, ctype, cdata)

  def typeOf[T: WeakTypeTag](t: T): Type = implicitly[WeakTypeTag[T]].tpe
  def use_account2[T: domain.account.e](acc: T): Any = {
    import domain.account.get
    new String(get[domain.credential.data](acc).value)
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
}

object Main {

  def main(args: Array[String]): Unit = {
    println("Hi mate")
    test.test_use_account2()
  }

}
