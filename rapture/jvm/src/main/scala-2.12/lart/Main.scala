package lart

import
  scala.concurrent.{
    Future,
  },
  akka.{
    NotUsed,
  }
object Main {

  lazy val logger = org.slf4j.LoggerFactory.getLogger("Main")
  lazy val conf = new ConfDeco(com.typesafe.config.ConfigFactory.defaultApplication)
  final implicit class ConfDeco(val self: com.typesafe.config.Config) extends AnyVal {
    def httpServerInterface = self getString "rapture.server.host"
    def httpServerPort = self getInt "rapture.server.port"
    def defaultDbUrl = self getString "db.default"
    def dbUrl = self getString s"db.$defaultDbUrl"
  }
  object db {
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

  def main(args: Array[String]): Unit = {
    println("Hello this is so fun")
    import scala.concurrent.ExecutionContext.Implicits.global
    import scala.concurrent.Await
    import scala.concurrent.duration._
    implicit val acsys = akka.actor.ActorSystem("LeBobs")
    implicit val mat = akka.stream.ActorMaterializer()
    val fut = db.users.first
    Await.ready(fut, 5.seconds)
  }

}
