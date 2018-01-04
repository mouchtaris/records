package gv
package http

import
  akka.{
    NotUsed,
  },
  akka.http.scaladsl.model.{
    HttpRequest,
    HttpResponse,
    StatusCodes,
    Uri,
  },
  akka.http.scaladsl.server.{
    PathMatcher,
    PathMatcher0,
    PathMatchers,
  },
  akka.stream.{
    FlowShape,
  },
  akka.stream.scaladsl.{
    Keep,
    Source,
    Flow,
    GraphDSL,
    Partition,
    BroadcastHub,
    PartitionHub,
    Broadcast,
    Merge,
    ZipN,
  }

object Multiplexor {

  val NotFound = HttpResponse(
    status = StatusCodes.NotFound
  )

}

final case class Multiplexor(
  routes: (Uri.Path, Handler)*
) {

  import GraphDSL.Implicits._

  val handlersWithMatchers = routes.toVector
    .map {
      case (prefix, handler) ⇒
        val matcher = PathMatcher(prefix, ())
        (matcher, handler)
    }

  val handlers: Vector[Handler] = handlersWithMatchers map (_ _2)
  val matchers: Vector[PathMatcher[Unit]] = handlersWithMatchers map (_ _1)

  def adaptPath(request: HttpRequest)(path: Uri.Path): HttpRequest =
      request withUri (request.uri withPath path)

  val handlerFilter: PathMatcher[Unit] ⇒ HttpRequest ⇒ Option[HttpRequest] = {
    matcher ⇒ request ⇒
      val path = request.uri.path
      matcher(path) match {
        case PathMatcher.Matched(rest, _) ⇒
          Some(adaptPath(request)(rest))
        case _ ⇒
          None
      }
  }

  val filterFlows: Vector[Flow[HttpRequest, Option[HttpRequest], NotUsed]] =
    matchers
      .map(handlerFilter)
      .map(Flow fromFunction _)

  type OptionalHandler = Flow[Option[HttpRequest], Option[HttpResponse], NotUsed]

  val optionalRequestFlow = Flow[Option[HttpRequest]]

  def optionalHandlerFlow(handler: Handler): OptionalHandler =
    optionalRequestFlow
      .collect { case Some(req) ⇒ req }
      .via(handler)
      .map(Some(_))

  val notForThisHandlerFlow: OptionalHandler =
    optionalRequestFlow
      .collect { case None ⇒ None }

  val handlerFlows: Vector[OptionalHandler] =
    handlers map { handler ⇒
      Flow fromGraph GraphDSL.create() { implicit b ⇒
        val toHandler = b add optionalHandlerFlow(handler)
        val nothing = b add notForThisHandlerFlow
        val bcast = b add Broadcast[Option[HttpRequest]](2)
        val merge = b add Merge[Option[HttpResponse]](2)

        bcast ~>  toHandler ~>  merge
        bcast ~>  nothing   ~>  merge

        FlowShape(bcast.in, merge.out)
      }
    }

  val foldFlow: Flow[Seq[Option[HttpResponse]], HttpResponse, NotUsed] =
    Flow fromFunction { maybeResponses: Seq[Option[HttpResponse]] ⇒
      maybeResponses
        .collectFirst { case Some(response) ⇒ response }
        .getOrElse(Multiplexor.NotFound)
    }

  val handler: Handler = Flow fromGraph GraphDSL.create() { implicit b ⇒ import GraphDSL.Implicits._
    val bcast = b add Broadcast[HttpRequest](filterFlows.length)
    val filters = filterFlows map (b add)
    val handlers = handlerFlows map (b add)
    val zip = b add ZipN[Option[HttpResponse]](filterFlows.length)
    val fold = b add foldFlow

    filters zip handlers foreach {
      case (filter, subHandler) ⇒
        bcast ~> filter ~> subHandler ~> zip
    }

    zip.out ~> fold

    FlowShape(bcast.in, fold.out)
  }
}
