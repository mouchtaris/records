package lart.http.routes

import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.{Directives, Route, _}
import hm.SomeoneFor.A
import org.slf4j.Logger
import Directives._

import scala.concurrent.{Future, Promise}
import scala.util.Success

final class CompleteFuture(
  pathPrefix: String
)(
  implicit
  _logger: A[Logger]#For[CompleteFuture]
) {
  import _logger.{value ⇒ logger}

  private[this] lazy val promise: Promise[Unit] = Promise()

  lazy val complete: Future[Unit] = promise.future

  lazy val route: Route =
    Directives.pathPrefix(pathPrefix) {
      logger.info("Completing future from {}")
      promise.tryComplete(Success(()))
      Directives.complete(StatusCodes.OK)
    }
}
