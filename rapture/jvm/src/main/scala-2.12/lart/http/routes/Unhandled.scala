package lart
package http
package routes

import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import org.slf4j.Logger
import hm.SomeoneFor.A

final class Unhandled(
  implicit
  _logger: A[Logger]#For[Unhandled]
)
{
  import _logger.{value ⇒ logger}

  lazy val route: Route =
    extractRequest { req ⇒
      logger.warn("Unhandled request: {}", req)
      complete(StatusCodes.NotFound)
    }

}
