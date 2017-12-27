package incubator

import
  gv.{fun, lang, list, string, tag, types, record},
  list._,
  list.op._,
  string._,
  tag._,
  fun._,
  types._,
  lang._,
  record._

import
  scala.reflect.{ClassTag, classTag},
  scala.concurrent.{
    Future,
    Await,
    ExecutionContext,
    duration,
  },
  scala.reflect.runtime.universe.{Select ⇒ _, _},
  implicit_tricks._,
  package_named._,
  package_record._,
  package_weird._,
  package_evidence._,
  Incubator.{
    account,
    timestamped,
  }

object wat {

  val vals =
    true ::
      account.email("email@email.com") ::
      account.password("password") ::
      account.authentication("AUTHORIZENTICATED") ::
      timestamped.createdAt(12) ::
      timestamped.modifiedAt(28) ::
      Nil

  import ExecutionContext.Implicits.global

  def `DO SOMETHING!!!`: Unit = {
    val evian = (account + timestamped).evidence(vals)
    println { evian.getters(timestamped.createdAt)(vals) }
    println { vals.got(account.email) }
    println { vals.got(timestamped.createdAt) }
    doSomething(vals)
  }


  val rec2 = account + timestamped
  object LOLAS extends Record {
    object poo extends int
  }
  private[this] def doSomething[vs <: List: rec2.Evidence](vs: vs) = {
    import rec2._
    println { vs.got(account.email) }
    println { vs.got(timestamped.createdAt) }
  }

}
