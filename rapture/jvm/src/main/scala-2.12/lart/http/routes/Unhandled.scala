package lart
package http
package routes

import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import org.slf4j.Logger

trait Unhandled extends Any {

  protected[this] def logger: Logger

  final def route: Route = {
    val _logger = logger
    extractRequest { req ⇒
      _logger.warn("Unhandled request: {}", req)
      complete(StatusCodes.NotFound)
    }
  }

}
