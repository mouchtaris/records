package lart

import akka.http.scaladsl.model.{HttpEntity, HttpMethods, HttpRequest, HttpResponse, Uri}
import akka.stream.scaladsl.{Sink, Source}

import scala.concurrent.{Await, Future}
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration._

object Main {
  def ringo() = {
    import org.ringojs.tools,
      tools.launcher.Main,
      tools.RingoRunner
    val rr = new RingoRunner()
    rr.run(Array("interactive"))
  }

  def main(args: Array[String]): Unit = {
    main0(args)
  }

  /** Configuration */
  implicit lazy val config = hm.config()

  object db {
    lazy val conf = new ConfDeco(com.typesafe.config.ConfigFactory.defaultApplication)
    final implicit class ConfDeco(val self: com.typesafe.config.Config) extends AnyVal {
      def defaultDbUrl = self getString "db.default"
      def dbUrl = self getString s"db.$defaultDbUrl"
    }
    import slick.jdbc.PostgresProfile.api.{
      Database,
      Tag,
      Table,
      TableQuery,
      longColumnType,
      stringColumnType,
      _
    }
    lazy val db = Database forURL conf.dbUrl

    class User(tag: Tag) extends Table[(Long, String)](tag, "users") {
      def id = column[Long]("id")
      def email = column[String]("email")
      def * = (id, email)
    }
    import db.run
    object users extends TableQuery[User](new User(_)) {
      def first: Future[Seq[(Long, String)]] = run(take(1).result)
      def by_email(e: String) =
        {
          val q = for {
            u ← this if u.email === e
          }
            yield u
          run(q.result)
        }
    }
  }

  def main0(args: Array[String]): Unit = {
    // println("Hello this is so fun")
    // logger.info("Hello from the logger too")
    // val fut = db.users.first
    // fut.onComplete(println)
    //_server.bind().map { _ ⇒ acsys.terminate() }

    val scene = setup.Scene()
    val logger = scene.loggingContext.factory(this.toString)

    val reqLogin = HttpRequest(
      uri = Uri("/v1/oauth/token"),
      method = HttpMethods.POST,
      entity = HttpEntity(
        s"""
           |{
           |  "token_type": "password",
           |  "password": "12121212",
           |  "email": "sponge@bob.com"
           |}
         """.stripMargin
      )
    )
    import scene.akkaContext._
    def send(req: HttpRequest): Future[HttpResponse] =
      Source.single(req)
        .via(scene.httpServer.handlerFlow)
        .runWith(Sink.head)
    val done = for {
//      resLogin ← send(reqLogin)
      user ← db.users.by_email("mouchtaris@gmail.com")
      done ← actorSystem.terminate()
    } yield {
//      println(resLogin)
      println(user)
    }
    Await.ready(done, 120.seconds)
    actorSystem.terminate()
    // scene.tcpHandler
    logger.info("Hello lol. But we are done.")
  }

}
