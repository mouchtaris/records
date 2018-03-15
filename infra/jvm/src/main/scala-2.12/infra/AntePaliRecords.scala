package infra

import
  scala.util.control.Exception.catching,
  scala.concurrent.{
    Await,
    duration,
    ExecutionContext,
    Future,
    TimeoutException,
  },
  duration._,
  akka.http.scaladsl.Http,
  akka.http.scaladsl.server._,
  Directives._,
  akka.actor.{
    ActorSystem,
    Terminated,
  },
  akka.stream.ActorMaterializer


object AntePaliRecords {
  val ahileas = "697_56_64_370"

  def main(args: Array[String]): Unit = {
    println("Ante pali")
    println(1 :: "hello" :: true :: Nil)
    println(AntePaliRecordsLib.tests.poo)

    val httpServer = new HttpServer
    catching(classOf[TimeoutException])
      .toTry
      .andFinally(httpServer.terminate()) {
        Await.ready(Future.never, 5 seconds)
      }
  }

  object HttpServer {
    val route: Route = {
      path("test") {
        complete("hell")
      }
    }
  }
  class HttpServer {
    import HttpServer.route

    implicit val actorSystem: ActorSystem = ActorSystem("AntePali")
    implicit val mat: ActorMaterializer = ActorMaterializer()
    val http = Http()
    val binding: Future[Http.ServerBinding] = Http().bindAndHandle(route, "0.0.0.0", 8082)

    import actorSystem.dispatcher

    def beginTermination(): Future[Terminated] =
      binding
        .flatMap(_.unbind())
        .flatMap(_ ⇒ actorSystem.terminate())

    def terminate(): Terminated =
      Await result (beginTermination(), 5 seconds)

  }

}

