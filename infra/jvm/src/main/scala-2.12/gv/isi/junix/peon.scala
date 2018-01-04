package gv
package isi.junix

import
  java.nio.{
    ByteBuffer,
  },
  java.nio.file.{
    Path,
    FileSystem,
    FileSystems,
  },
  scala.collection.mutable,
  scala.concurrent.{
    Future,
    Await,
    ExecutionContext,
    Promise,
  },
  scala.concurrent.duration._,
  scala.util.{
    Success,
    Failure,
    Try,
  },
  akka.{
    Done,
    NotUsed,
  },
  akka.http.scaladsl.{
    Http,
  },
  akka.http.scaladsl.model.{
    HttpRequest,
    HttpResponse,
    Uri,
    StatusCodes,
    HttpEntity,
  },
  akka.http.scaladsl.server.{
    Route,
    RouteResult,
    Directives,
  },
  akka.http.scaladsl.client.{
    RequestBuilding,
  },
  akka.stream.{
    Materializer,
    ActorMaterializer,
    UniformFanOutShape,
    IOResult,
  },
  akka.stream.scaladsl.{
    Source,
    Flow,
    Sink,
    Keep,
    GraphDSL,
    Partition,
    FileIO,
  },
  akka.actor.{
    Actor,
    ActorRef,
    ActorSystem,
    Props,
  },
  akka.pattern.{
    ask,
    pipe,
  },
  akka.util.{
    Timeout,
    ByteString,
  },
  com.typesafe.config.{
    Config ⇒ tsConfig,
  },
  gv.{
    string,
    akkadecos,
    lang,
  },
  string._,
  akkadecos._,
  lang._

object peon {

  object Config {

    final val ARCH = "arch"
    final val APOTHECARIUM = "apothecarium"

    final implicit class Ext(val root: tsConfig) extends AnyVal {
      def arch: Arch.Ext = root getConfig ARCH
      def apothecarium: Apothecarium.Ext = root getConfig APOTHECARIUM
    }

    object Arch {
      final val MIRROR = "mirror"

      final implicit class Ext(val root: tsConfig) extends AnyVal {
        def mirror: Uri = Uri(root getString MIRROR)
      }
    }

    object Apothecarium {
      final val STORAGE_PATH = "storage_path"

      final implicit class Ext(val root: tsConfig) extends AnyVal {
        def storagePath: String = root getString STORAGE_PATH
      }
    }

  }

}

final class peon(
  implicit
  system: ActorSystem = ActorSystem("Leontas"),
  config: peon.Config.Ext
) {
  import peon._


  implicit val materializer: Materializer = ActorMaterializer()
  implicit val timeout: Timeout = 5.seconds
  val http = Http()
  val archMirror: Flow[(HttpRequest, NotUsed), (Try[HttpResponse], NotUsed), Http.HostConnectionPool] =
    http.cachedHostConnectionPool(config.arch.mirror.authority.host.address)
  implicit val apothecariumConfig: Config.Apothecarium.Ext = config.apothecarium

  final case class State(
    unhandledHttpRequests: mutable.Buffer[HttpRequest] = mutable.Buffer.empty
  )

  final case object Noted
  final case object Report
  final case class UnhandledHttpRequest(req: HttpRequest)
  final case object UnhandledHttpRequestsDone

  final class Stator extends Actor {
    val state = State()

    def report: String =
      s"""
         | ==== Unhandled Requests ====
         | *** ${state.unhandledHttpRequests.map(_.prettyInspect).mkString("\n  *** ")}
       """.stripMargin
    def receive: Receive = {
      case UnhandledHttpRequest(req) ⇒
        state.unhandledHttpRequests.append(req)
        sender ! Noted
      case Report ⇒
        sender ! report
    }
  }

  final val stator = system actorOf Props[Stator](new Stator)

  final class Apothecarium(
    implicit
    config: Config.Apothecarium.Ext
  )
    extends LocalException
  {
    val filesystem =  FileSystems.getDefault()
    val storagePath = filesystem getPath config.storagePath

    final class PathHandler(pathname: String) {
      val path = storagePath resolve pathname
      val file = path.toFile
      val isFile = file.isFile
      val lockPath = path.getParent resolve path.getFileName.toString + ".lock"
      val lockFile = lockPath.toFile
      val lockFileIsFile = lockFile.isFile

      final class FileLockExists extends Exception(s"File lock exists: $lockPath")
      val tryLock =
        if (lockFileIsFile)
          Failure(new FileLockExists)
        else
          Success(())

      val source = FileIO fromPath path
    }
  }

  sealed trait MirrorHandler
    extends AnyRef
    with LocalException
  {
    type CanHandle = String ⇒ Boolean
    val canHandle: CanHandle
    val baseUri: Uri

    final val apothecarium: Apothecarium = new Apothecarium

    final def handle(pathname: String, request: HttpRequest): Source[ByteString, Future[IOResult]] = {

      val handler = new apothecarium.PathHandler(pathname)
      if (handler.isFile)
        handler.source
      else
        handler.tryLock match {
          case Failure(lockEx) ⇒
            final case class FileLocked() extends Exception(s"File is locked: $pathname", lockEx)
            val ex = FileLocked()
            val mat = Future failed ex
            Source failed ex mapMaterializedValue { _ ⇒ mat }
          case Success(_) ⇒
            ???
        }
    }

    final def unapply(prefix: String): Boolean = canHandle(prefix)
  }

  def serve(mirrorType: String, request: HttpRequest, restPath: Uri.Path): Future[HttpResponse] =
    mirrorType match {
      case _ ⇒
        Future successful HttpResponse(StatusCodes.NotFound, entity = HttpEntity(s"Cannot serve $mirrorType"))
    }

  val route: Route = {
    import Directives._
    import akka.http.scaladsl.model._

    val redirectLocation = HttpHeader.parse("Location", "./hello") match {
      case HttpHeader.ParsingResult.Ok(header, _) ⇒ header
    }
    val redirectHeaders = scala.collection.immutable.Seq(redirectLocation)

    val hello =
      get {
        path("hello") {
          complete("hi")
        } ~
        path("redirect") {
          complete(HttpResponse(
            status = StatusCodes.Found,
            headers = redirectHeaders
          ))
        }
      }
    val report =
      (get & path("report")) {
        onSuccess(stator.ask(Report).mapTo[String]) { report ⇒
          complete(report)
        }
      }
    val arch =
      (get & pathPrefix(Segment) & extractRequest & extractUnmatchedPath) { (typ, request, rest) ⇒
        complete(serve(typ, request, rest))
      }
    val unhandled =
      extractRequest { req ⇒
        val reply = stator ? UnhandledHttpRequest(req)
        onSuccess(reply) { _ ⇒
          complete("yo")
        }
      }

    hello ~
      report ~
      arch ~
      unhandled
  }

}
