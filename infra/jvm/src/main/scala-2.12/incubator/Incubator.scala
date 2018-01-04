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
  implicit val peonconf: junix.peon.Config.Ext = conf getConfig "gv.peon"
  implicit val leonconf = junix.leon.config.Config(conf getConfig "gv.leon")
  implicit val httpExt = Http()
  val Leon = new junix.leon.Leon
  val handler: http.Handler = {
    val toPrefix: String ⇒ Uri.Path = Uri.Path / _
    val lamepm: http.Handler = Flow[HttpRequest] map { req ⇒ HttpResponse(entity = req.uri.toString) }
    val handlers: Seq[(Uri.Path, http.Handler)] = Seq[(String, http.Handler)](
      "leon" → Leon.httpHandler,
      "peon" → (new junix.peon).route,
      "lamepm" → lamepm,
    )
      .map { case (s, h) ⇒ toPrefix(s) → h }
    val mplex = http.Multiplexor(handlers: _*)
    mplex.handler
    Leon.httpHandler
  }
  val onlyOne = true
  val server = infra.blue.http.Server(
    serveOne = onlyOne,
    handler = handler,
  )

  def main(args: Array[String]): Unit = try {
    import typebug.sinks.out
    import ExecutionContext.Implicits.global
    db;

//    new gv.codegen.Codegen("/templates/manifest.yaml", "infra").run()
//    return

    val (started, ended) = server.start()
    val requests = Vector(
        "http://localhost:8080/index.html",
        "http://localhost:8080/peon/hello",
        "http://localhost:8080/peon/nowhere",
        "http://localhost:8080/peon/redirect",
        "http://localhost:8080/leon/arch/LOLIS",
        "http://localhost:8080/leon/lame/ThisIsThEEnd",
        "http://localhost:8080/lamepm/ThisIsTheFelnd",
        "http://localhost:8080/leon/lame/ThisIsTheFelnd",
      )
        .++((1 to 34) map ("http://localhost:8080/leon/arch/LOL" +))
        .++((1 to 43) map ("http://localhost:8080/arch/LOL" +))
      .map(RequestBuilding.Get(_))
      .map(httpExt.singleRequest(_))

    val download = Source(requests)
      .mapAsync(1)(identity)
      .toMat(Sink.collection[HttpResponse, Vector[HttpResponse]])(Keep.right)

    val done = started
      .flatMap { _ ⇒ download.run().map(_ foreach println) }
      .flatMap { _ ⇒ println("downloading done"); ended }
      .flatMap { _ ⇒ println("ending done"); Future successful (()) }
    Await.result(done, if (onlyOne) 4.seconds else Duration.Inf)
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
