package incubator

import
  scala.reflect.{ClassTag, classTag},
  scala.util.{ Success },
  scala.concurrent.{ Future, Await, ExecutionContext },
  scala.concurrent.duration._,
  com.typesafe.config.{
    Config,
    ConfigFactory,
  },
  akka.actor.{
    ActorSystem,
  },
  akka.stream.{
    ActorMaterializer,
  },
  gv.{fun, lang, list, string, tag, types, record, config, util},
  list._,
  list.op._,
  string._,
  tag._,
  fun._,
  types._,
  lang._,
  record._,
  config._,
  util._

import
  scala.reflect.runtime.universe.{Select ⇒ _, _},
  implicit_tricks._,
  package_named._,
  package_record._,
  package_weird._,
  package_evidence._

object package_evidence

object package_weird {

  trait ZipLists[a <: List, b <: List, out <: List] {
    def apply(a: a, b: b): out
  }

  object NilZip extends ZipLists[List, List, Nil] {
    def apply(a: List, b: List): Nil = Nil
    def apply[a <: List, b <: List](): ZipLists[a, b, Nil] = asInstanceOf[ZipLists[a, b, Nil]]
  }

  implicit def zipNilA[b <: List]: ZipLists[Nil, b, Nil] = NilZip()
  implicit def zipNilB[a <: List]: ZipLists[a, Nil, Nil] = NilZip()

  implicit def zipList[a, ta <: List, b, tb <: List, tzip <: List](
    implicit
    tzip: ZipLists[ta, tb, tzip],
  ): ZipLists[a :: ta, b :: tb, (a, b) :: tzip] = {
    case (a :: ta, b :: tb) ⇒
      (a, b) :: tzip(ta, tb)
  }

}

object Incubator {

  trait account extends Record {
    case object email extends string
    case object password extends string
    case object authentication extends string
    type Fields = email.type :: password.type :: authentication.type :: Nil
  }
  object account extends account

  trait timestamped extends Record {
    case object createdAt extends int
    case object modifiedAt extends int
    type Fields = createdAt.type :: modifiedAt.type :: Nil
  }
  object timestamped extends timestamped

  implicit val actorSystem = ActorSystem("Actoriliki")
  implicit val materializer = ActorMaterializer()
  val conf = ConfigFactory.defaultApplication()
  val dburi = conf getString "db.pat.staging"
  def main(args: Array[String]): Unit = try {
    import typebug.sinks.out
    wat.`DO SOMETHING!!!`

//    import slick.dbio.DBIO
//    import me.musae.detail.slick.Tables
//    import Tables.profile.api._
//    import ExecutionContext.Implicits.global
//    println {
//      Await.result(db.run(Tables.Users.take(1).result), 10.seconds)
//    }


//    new gv.codegen.Codegen("/templates/manifest.yaml", "shared").run()

    val li = (1, "Hello", true).toList
    typebug.inspect[li.type]
    val tu = li.toTuple
    typebug.inspect[tu.type]

    import me.musae.detail.slick.Tables
    import me.musae.model._
    import Tables.profile.api._
    Tables.users.withFilter(_.col(user.id) > 2)
  }
  finally {
    db.close()
    actorSystem.terminate()
  }

  def db = slick.jdbc.JdbcBackend.Database.forConfig("db.musae.local")

}

object package_named

object package_record

object implicit_tricks {

  trait ImplicitConstruct[list <: List] extends Any {
    def apply(): list
  }
  trait ImplicitConstructDeductions {
    final implicit val implicitConstructNil: ImplicitConstruct[Nil] =
      () ⇒ Nil
    final implicit def implicitConstruct[h: Implicitly, t <: List: ImplicitConstruct]: ImplicitConstruct[h :: t] =
      () ⇒ implicitly[h] :: implicitly[ImplicitConstruct[t]].apply()
  }
  object ImplicitConstruct
    extends AnyRef
      with ImplicitConstructDeductions

  def the[t <: AnyRef](implicit t: t): t.type = t
}
