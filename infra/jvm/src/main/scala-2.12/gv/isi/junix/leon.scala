package gv
package isi.junix

import
  scala.collection.mutable,
  scala.concurrent.{
    Future,
    Await,
    ExecutionContext,
    Promise,
  },
  scala.concurrent.duration._,
  akka.{
    Done,
    NotUsed,
  },
  akka.http.scaladsl.model.{
    HttpRequest,
    HttpResponse,
    Uri,
  },
  akka.http.scaladsl.server.{
    Route,
    RouteResult,
    Directives,
  },
  akka.stream.{
    Materializer,
    ActorMaterializer,
  },
  akka.stream.scaladsl.{
    Flow,
    Sink,
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
  }

final class leon(
  implicit
  system: ActorSystem = ActorSystem("Leontas")
) {

  implicit val materializer: Materializer = ActorMaterializer()
  implicit val timeout: Timeout = 5.seconds

  case class State(
    unhandledHttpRequests: mutable.Buffer[HttpRequest] = mutable.Buffer.empty
  )

  case object Noted
  case object Report
  case class UnhandledHttpRequest(req: HttpRequest)
  case object UnhandledHttpRequestsDone

  class Stator extends Actor {
    val state = State()

    def report: String =
      s"""
         | ==== Unhandled Requests ====
         | *** ${state.unhandledHttpRequests.mkString("\n  *** ")}
       """.stripMargin
    def receive: Receive = {
      case UnhandledHttpRequest(req) ⇒
        state.unhandledHttpRequests.append(req)
        sender ! Noted
      case Report ⇒
        sender ! report
    }
  }

  val stator = system actorOf Props[Stator](new Stator)

  def routes(prefix: String): Route = {
    import Directives._

    val hello =
      (get & path("hello")) {
        complete("hi")
      }
    val report =
      (get & path("report")) {
        onSuccess(stator.ask(Report).mapTo[String]) { report ⇒
          complete(report)
        }
      }
    val unhandled =
      extractRequest { req ⇒
        val reply = stator ? UnhandledHttpRequest(req)
        onSuccess(reply) { _ ⇒
          complete("yo")
        }
      }

    pathPrefix(prefix) {
      hello ~
        report ~
        unhandled
    }
  }

}
