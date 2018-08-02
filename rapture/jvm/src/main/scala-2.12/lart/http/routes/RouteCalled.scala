package lart
package http.routes

import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.Directives.{complete, pathPrefix ⇒ pathPrefixDirective}
import akka.http.scaladsl.server.Route

import scala.concurrent.Promise
import scala.util.Success

object RouteCalled {

  def apply(
    pathPrefix: String,
    completionPromise: Promise[Unit],
    logger: Logging.Logger,
  )
  : Route =
    pathPrefixDirective(pathPrefix) {
      logger.info("Completing future from {}")
      val result =
        if (completionPromise.tryComplete(Success(())))
          StatusCodes.OK
        else
          StatusCodes.AlreadyReported
      complete(result)
    }

}
