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
  }

object Server {
  type Started = Started.type
  type Ended = Ended.type
  object Started
  object Ended
}

class Server(
  handler: Handler,
  completeSignal: Future[NotUsed],
  requestServed: Sink[(HttpRequest, HttpResponse), Future[Any]],
)(
  implicit
  http: HttpExt,
  materializer: Materializer,
  config: Config.Ext,
) {

  val routeFlow: Flow[HttpRequest, HttpResponse, NotUsed] =
    handler
      .named("HTTP Request Handler Flow")

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

  def start(): (Future[Server.Started], Future[Server.Ended]) = {
    import http.system.dispatcher
    val binding = http
      .bindAndHandle(
        interface = config.interface,
        port = config.port,
        handler = snifflingFlow,
      )
    val started = binding map (_ ⇒ Server.Started)
    val ended = binding
      .zip(completeSignal)
      .flatMap {
        case (binding, _) ⇒
          binding.unbind()
      }
      .map(_ => Server.Ended)
    (started, ended)
  }
}
