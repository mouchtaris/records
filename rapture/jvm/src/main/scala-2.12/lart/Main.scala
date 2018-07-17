package lart

import scala.concurrent.{ExecutionContext, Future}
import akka.NotUsed
import akka.actor.ActorSystem
import akka.dispatch.ExecutionContexts
import akka.stream.Materializer
import org.slf4j.LoggerFactory.getLogger
import hm.SomeoneFor.A

object Main {

  lazy val logger = getLogger("Main")
  lazy val config = hm.config()
  object db {
    lazy val conf = new ConfDeco(com.typesafe.config.ConfigFactory.defaultApplication)
    final implicit class ConfDeco(val self: com.typesafe.config.Config) extends AnyVal {
      def defaultDbUrl = self getString "db.default"
      def dbUrl = self getString s"db.$defaultDbUrl"
    }
    import slick.jdbc.PostgresProfile.api._
    lazy val db = Database forURL conf.dbUrl

    class User(tag: Tag) extends Table[(Long, String)](tag, "users") {
      def id = column[Long]("id")
      def email = column[String]("email")
      def * = (id, email)
    }
    import db.run
    object users extends TableQuery[User](new User(_)) {
      def first = run(take(1).result)
    }
  }

  implicit val acsys: ActorSystem = akka.actor.ActorSystem("LeBobs")
  implicit val mat: Materializer = akka.stream.ActorMaterializer()
  val server: http.Server  = {
    import http.Server
    import http.routes._
    implicit val _logger1 = A(getLogger("CompleteFuture")).for_[CompleteFuture]
    implicit val _logger2 = A(getLogger("Unhandled")).for_[Unhandled]
    implicit val conf = config.server
    implicit val _ec = A[ExecutionContext](ExecutionContexts.global()).for_[Server]
    val route1 = new CompleteFuture("complete")
    val route2 = new Unhandled
    val routes = Seq(route1.route, route2.route)
    val server = new Server(routes, route1.complete)
    server
  }

  def main(args: Array[String]): Unit = {
    println("Hello this is so fun")
    logger.info("Hello from the logger too")
    import scala.concurrent.ExecutionContext.Implicits.global
    import scala.concurrent.Await
    import scala.concurrent.duration._
    val fut = db.users.first
    fut.onComplete(println)
    server.bind()
      .map { _ ⇒
        acsys.terminate()
      }
  }

}
