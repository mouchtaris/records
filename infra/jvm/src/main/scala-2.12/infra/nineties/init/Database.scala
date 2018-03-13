package infra
package nineties
package init

import
  slick.jdbc.JdbcBackend.{
    Database ⇒ sDatabase,
    DatabaseDef,
  },
  gv.control

final case class Database()(
  implicit
  config: init.Config
)
  extends Init[DatabaseDef]
{

  def initialize(): Result =
   config map { _.db.url } map { sDatabase forURL _ }

  override val close: Close =
    db ⇒ control(db.close()).future

}


