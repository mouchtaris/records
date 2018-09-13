package incubator

import
  java.nio.charset.StandardCharsets.{ UTF_8 },
  scala.concurrent._,
  scala.concurrent.duration._,
  scala.util._,
  akka.stream._,
  akka.stream.scaladsl._,
  akka.http.scaladsl._,
  akka.http.scaladsl.model._,
  akka.http.scaladsl.client._,
  akka.http.scaladsl.server._,
  akka.actor._,
  akka._,
  akka.util._,
  com.typesafe.config._,
  gv.isi.junix.{ leon ⇒ leo },
  gv.akkadecos.{
    FlowBalanceDecoration,
  },
  gv.http.{
    Handler,
  }

object totost {

  implicit val actorSystem = ActorSystem("Bobs")
  implicit val materializer = ActorMaterializer()
  implicit val http = Http()
  val conf = ConfigFactory.defaultApplication
  implicit val leonConf = leo.config.Config(conf getConfig "gv.leon")
  import actorSystem.dispatcher

  val KEEP_SERVING = conf getBoolean "totost.keep_serving"

  def toString(source: Source[ByteString, Any]): Future[String] =
    source
      .runWith(Sink.fold(StringBuilder.newBuilder) { (sb, bs: ByteString) ⇒ sb.appendAll(bs.utf8String) })
      .map(_.result())

  def termination(u: Unit): Future[Unit] = {
    println(" *** Terminating actor system")
    actorSystem.terminate() map println
  }


  def main(args: Array[String]): Unit = {
    main1(args)
  }

  def main0(args: Array[String]): Unit = {
    Source
      .unfoldAsync(0) { previous ⇒
        Thread.sleep(1000)
        val state = previous + 1
        val element = previous
        val result = state → element
        Future successful Some(result)
      }
      .take(50)
      .runWith(Sink foreach println)
  }

  def main1(args: Array[String]): Unit = {
    val leon = new leo.Leon
    val dlm = leon.DownloadManager
    val httpExt = leon.httpExt
    import leo.mirror.DownloadingMissingPackageHandler.Context

    val allDonePromise = Promise[Unit]()
    val allDone = allDonePromise.future

    val lolHandler: Flow[HttpRequest, HttpResponse, NotUsed] =
      Flow.fromFunction { (request: HttpRequest) ⇒
        actorSystem.log debug s"FALSE SERVING $request"
        Thread.sleep(1000)
        Future.successful(())
          .map { _ ⇒
            HttpResponse(
              status = StatusCodes.OK,
              entity = s"FUCK OFF ${request.uri}"
            )
          }
      }
      .mapAsync(100)(identity)

    val leonHandlers: Seq[(Uri.Path, Handler)] = Seq(
      Uri.Path./("leon") → leon.httpHandler,
    )
    val leonHandler = new gv.http.Multiplexor(leonHandlers: _*).handler
    val handlers8081: Seq[(Uri.Path, Handler)] = Seq(
      Uri.Path./("mlol") → lolHandler,
    )
    val handler8081 = new gv.http.Multiplexor(handlers8081: _*).handler

    val futureBinding8080 = http.bindAndHandle(
      handler = leonHandler,
      interface = leonConf.server.host,
      port = leonConf.server.port,
    )
    val futureBinding8081 = http.bindAndHandle(
      handler = handler8081,
      interface = "0.0.0.0",
      port = 8081
    )
    val futureBinding = futureBinding8080.zip(futureBinding8081)

    val base = Uri("http://localhost:8080/leon/lol/")

    val requests =
//      (1 to 5).toStream
    Vector.empty[Int]
        .map { i ⇒ if (i == 33) allDonePromise.complete(Success(())); i }
        .map(_ toString)
        .map(base.path +)
        .map(base withPath)
        .map(RequestBuilding Get _)

    val source = Source(requests)
    val flo1 = Flow[HttpRequest]
      .mapAsyncUnordered(8)(httpExt.singleRequest(_: HttpRequest))
      .mapAsyncUnordered(0) { response ⇒
        toString(response.entity.dataBytes) map (response.toString + ":" + _)
      }
    val flo2 = Flow[HttpRequest]
      .mapAsync(1) { r ⇒ http.singleRequest(r) }
      .map { r ⇒ r.toString + toString(r.entity.dataBytes) }
    val flo = flo2

    def futureResponses = source.via(flo).runWith(Sink.collection)
    val theend = futureBinding
      .flatMap {
        case (b8080, b8081) ⇒
          println(" *** Server started")
          futureResponses map { resps ⇒
            println(" *** Responses came in")
            (b8080, b8081, resps)
          }
      }
      .flatMap {
        case (b8080, b8081, resps) ⇒
          resps foreach println
          println(" *** Begining unbind")
          if (KEEP_SERVING)
            Future.never
          else
            b8080.unbind().zip(b8081.unbind())
            .map { _ ⇒
              println(" *** Unbound")
            }
      }
      .flatMap(termination)

    if (!KEEP_SERVING)
      control.Exception.catching(classOf[java.util.concurrent.TimeoutException])
        .withTry(Await.result(theend, 5.seconds))
        .recover {
          case ex ⇒
            Await.result(termination(ex.printStackTrace()), 5.seconds)
        }
    println("Goodbye")
  }

}
