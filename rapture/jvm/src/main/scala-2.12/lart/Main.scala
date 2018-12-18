package lart

object Main {

  /** Configuration */
  implicit lazy val config = hm.config()

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

  def main(args: Array[String]): Unit = {
    // println("Hello this is so fun")
    // logger.info("Hello from the logger too")
    // val fut = db.users.first
    // fut.onComplete(println)
    //_server.bind().map { _ ⇒ acsys.terminate() }

    val scene = setup.Scene()
    val logger = scene.loggingContext.factory(this.toString)
    scene.httpServer.start()
    // scene.tcpHandler
    logger.info("Hello lol")
  }

}
