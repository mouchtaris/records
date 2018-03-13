package infra
package nineties
package details
package slick

trait Tables {

  val profile: _root_.slick.jdbc.JdbcProfile

  import profile.api._

  class User(tag: Tag) extends Table[(Int, String)](tag, "users") {
    def id = column[Int]("id")
    def email = column[String]("email")
    def * = (id, email)
  }

  object users extends TableQuery(new User(_)) {
  }
}

object Tables extends Tables {
  val profile = _root_.slick.jdbc.PostgresProfile
}
