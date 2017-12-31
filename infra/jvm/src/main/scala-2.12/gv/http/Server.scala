package gv
package http

import
  scala.concurrent.{
    Future,
  },
  akka.{
    NotUsed,
  },
  akka.http.scaladsl.{
    Http,
    HttpExt,
  },
  akka.http.scaladsl.model.{
    HttpRequest,
    HttpResponse,
  },
  akka.http.scaladsl.server.{
    Route,
    RouteResult,
  },
  akka.stream.{
    Attributes,
    FlowShape,
    Materializer,
  },
  akka.stream.scaladsl.{
    Flow,
    Sink,
    GraphDSL,
    Broadcast,
    Zip,
  },
  akka.actor.{
    ActorSystem,
  },
  akkastream.{
    FlowDecoration,
  }

class Server(
  route: Route,
  completeSignal: Future[NotUsed],
  requestServed: Sink[(HttpRequest, HttpResponse), Future[Any]],
)(
  implicit
  system: ActorSystem,
  materializer: Materializer,
  config: Config.Ext,
) {

  val routeFlow: Flow[HttpRequest, HttpResponse, NotUsed] =
    RouteResult
      .route2HandlerFlow(route)
      .withName("HTTP Request Handler Flow")

  val snifflingFlow: Flow[HttpRequest, HttpResponse, NotUsed] =
    Flow.fromGraph {
      GraphDSL.create(
        routeFlow,
      ) { implicit b ⇒ rute ⇒
          import GraphDSL.Implicits._
          val repl = b add Broadcast[HttpRequest](2).withAttributes(Attributes(Attributes.Name("Http Request Splitter")))
          val merger = b add Zip[HttpRequest, HttpResponse].withAttributes(Attributes(Attributes.Name("Req Res Zip")))
          val repl2 = b add Broadcast[HttpResponse](2)
          val reqservd = b add requestServed

          repl.out(0)                                   ~>  merger.in0
          repl.out(1)   ~>  rute.in
                            rute.out  ~>  repl2.in
                                          repl2.out(0)  ~>  merger.in1
                                                            merger.out  ~>  reqservd.in
          FlowShape(repl.in, repl2.out(1))
      }
    }

  val http: HttpExt = Http()

  def start(): Future[Unit] = {
    import system.dispatcher
    http
      .bindAndHandle(
        interface = config.interface,
        port = config.port,
        handler = snifflingFlow,
      )
      .zip(completeSignal)
      .flatMap {
        case (binding, _) ⇒
          binding.unbind()
      }
  }
}
