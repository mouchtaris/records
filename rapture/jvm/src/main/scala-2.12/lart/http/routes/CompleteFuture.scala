package lart
package http.routes

import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.Directives.{ pathPrefix ⇒ pathPrefixDirective, complete }
import akka.http.scaladsl.server.Route

import scala.concurrent.{Future, Promise}
import scala.util.Success

object CompleteFuture {

  def apply(
    pathPrefix: String,
    logger: Logger.Logger,
  )
  : (Route, Future[Unit]) = {
    val promise: Promise[Unit] = Promise()
    val future: Future[Unit] = promise.future
    val route: Route =
      pathPrefixDirective(pathPrefix) {
        logger.info("Completing future from {}")
        promise.tryComplete(Success(()))
        complete(StatusCodes.OK)
      }
    (route, future)
  }
}
