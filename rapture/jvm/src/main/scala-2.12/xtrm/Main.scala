package xtrm

object Main {

  object domain extends mse.domain

  object async {
    trait Future[T] extends Any {

    }
  }

  object persist {

  }

  object op {

  }

  object accounting {
    import async._
    import persist._
    def signUp[Account: domain.Account.e](account: Future[Account]): Any = {
      ???
    }
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
  }

  def main(args: Array[String]): Unit = {
    println("Hi mate")
    test.test_use_account2()
  }

}
