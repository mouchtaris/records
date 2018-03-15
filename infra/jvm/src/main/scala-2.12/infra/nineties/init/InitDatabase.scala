package infra
package nineties
package init

import
  slick.jdbc.JdbcBackend.{
    Database,
  }

trait InitDatabase {
  this: InitPlace ⇒

  protected[this]
  final type DatabaseDef = slick.jdbc.JdbcBackend.DatabaseDef

  implicit val InitDatabase: Init[DatabaseDef] =
    () ⇒ config
      .map(_.db.url)
      .map(Database forURL _)

}
