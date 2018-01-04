package infra.blue
package http

import
  scala.util.{
    Success,
  },
  scala.concurrent.{
    Future,
    Promise,
  },
  akka.{
    Done,
    NotUsed,
  },
  akka.stream.{
    Materializer,
  },
  akka.stream.scaladsl.{
    Sink,
  },
  akka.http.scaladsl.{
    HttpExt,
  },
  akka.http.scaladsl.model.{
    HttpRequest,
    HttpResponse,
  },
  gv.{ http }

object Server {

  def apply(serveOne: Boolean, handler: http.Handler)(
    implicit
    config: Config.Ext,
    akkaHttp: HttpExt,
    materializer: Materializer,
  ): http.Server = {
    val completeSignal: Promise[NotUsed] = Promise()
    val requestServed: Sink[(HttpRequest, HttpResponse), Future[Done]] =
      Sink.foreach {
        case (req, res) ⇒
          println(s"HTTP\n  $req\n  $res")
          if (serveOne)
            completeSignal.tryComplete(Success(NotUsed))
      }
    implicit val httpConfig = config.toServerConfig
    new http.Server(handler, completeSignal.future, requestServed)
  }
}

