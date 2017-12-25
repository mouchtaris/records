package me.musae
package detail.slick

trait Tables
  extends AnyRef
  with record_table.MixIn
{
  val profile: slick.jdbc.JdbcProfile

  import profile.api._
//  import slick.model.ForeignKeyAction
//  import slick.collection.heterogeneous._
//  import slick.collection.heterogeneous.syntax._
  // NOTE: GetResult mappers for plain SQL are only generated for tables where Slick knows how to map the types of all columns.
//  import slick.jdbc.{ GetResult ⇒ GR }

  /** DDL for all tables. Call .create to execute. */
  lazy val schema: profile.SchemaDescription = Array(
    users.schema
  ).reduceLeft(_ ++ _)

  lazy val users = new TableQuery(RecordTable(model.users)(_))

}

object Tables extends Tables {
  val profile = slick.jdbc.PostgresProfile
}
