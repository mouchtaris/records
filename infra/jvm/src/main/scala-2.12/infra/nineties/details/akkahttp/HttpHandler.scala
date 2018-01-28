package infra
package nineties
package details
package akkahttp

import
  akka.{
    NotUsed,
  },
  akka.stream.scaladsl.{
    Flow,
  },
  akka.http.scaladsl.model.{
    HttpRequest,
    HttpResponse,
    StatusCodes,
  }

final case class HttpHandler() {

  val handler: Flow[HttpRequest, HttpResponse, NotUsed] =
    Flow fromFunction { _: HttpRequest ⇒
      HttpResponse(
        status = StatusCodes.BadRequest
      )
    }

}
