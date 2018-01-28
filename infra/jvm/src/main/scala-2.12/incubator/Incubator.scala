package incubator

import
  java.nio.charset.StandardCharsets.UTF_8,
  scala.reflect.{
    ClassTag,
    classTag,
  },
  scala.concurrent.{
    Await,
    ExecutionContext,
    Promise,
    Awaitable,
  },
  scala.concurrent.duration._,
  com.typesafe.config.{
    Config,
    ConfigFactory,
  },
  akka.http.scaladsl.server.{
    RequestContext,
  },
  gv.{akkadecos, http, fun, lang, list, string, tag, types, record, config, util},
  akkadecos._,
  list._,
  list.op._,
  string._,
  tag._,
  fun._,
  types._,
  lang._,
  record._,
  config._,
  util._,
  gv.isi.junix,
  gv.isi.junix.leon.{
    PackageSource,
  },
  akka.stream.{
    Attributes,
    ActorAttributes,
    ClosedShape,
  },
  akka.stream.scaladsl.{
    Balance,
    Merge,
    RunnableGraph,
  },
  akka.util.{
    Timeout,
  }

import
  scala.reflect.runtime.universe.{Select ⇒ _, Try ⇒ _,  _},
  implicit_tricks._,
  package_named._,
  package_record._,
  package_weird._,
  package_evidence._,
  package_async._

object package_evidence

object package_async {

  final implicit class AwaitDecoration[T](val self: Awaitable[T]) extends AnyVal {
    def await(implicit tm: Timeout) = Await ready (self, tm.duration)
  }

}

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

  implicit val actorSystem = ActorSystem("Actoriliki")
  implicit val materializer = ActorMaterializer()
  implicit val timeout = Timeout(14 seconds)
  val conf = ConfigFactory.defaultApplication()
  val dburi = conf getString "db.pat.staging"
  lazy val db = slick.jdbc.JdbcBackend.Database.forConfig("db." + conf.getString("db.default"))
  implicit val httpconf: infra.blue.http.Config.Ext = conf
  implicit val httpExt = Http()

  def main(args: Array[String]): Unit = try {
    import typebug.sinks.out
    import ExecutionContext.Implicits.global
    db;



//    new gv.codegen.Codegen("/templates/manifest.yaml", "infra").run()
//    return
  }
  finally {
    db.close()
    actorSystem.terminate()
  }

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
