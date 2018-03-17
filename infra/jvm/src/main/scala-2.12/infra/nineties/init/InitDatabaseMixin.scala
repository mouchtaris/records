package infra
package nineties
package init

import
  slick.jdbc.JdbcBackend.{
    Database,
  }

trait InitDatabaseMixin {
  this: InitPlace ⇒

  protected[this]
  final type DatabaseDef = slick.jdbc.JdbcBackend.DatabaseDef

  final type InitDatabase = Init[DatabaseDef]
  implicit val InitDatabase: InitDatabase =
    () ⇒ config
      .map(_.db.url)
      .map(Database forURL _)

}
