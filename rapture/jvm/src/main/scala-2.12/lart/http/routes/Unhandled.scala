package lart
package http
package routes

import akka.http.scaladsl.model.{HttpRequest, HttpResponse, StatusCodes}
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route

import scala.concurrent.Future

object Unhandled {

  def apply(
    handle: HttpRequest ⇒ Future[HttpResponse],
    logger: Logger.Logger
  ): Route =
    extractRequest { req ⇒
      logger.warn("Unhandled request: {}", req)
      complete(handle(req))
    }

}
