package me.musae
package detail.slick

import
  gv.{
    slick ⇒ dslick,
  }

trait Tables
  extends AnyRef
    with dslick.record_table.MixIn
{
  import profile.api._
  //  import slick.model.ForeignKeyAction
  //  import slick.collection.heterogeneous._
  //  import slick.collection.heterogeneous.syntax._
  // NOTE: GetResult mappers for plain SQL are only generated for tables where Slick knows how to map the types of all columns.
  //  import slick.jdbc.{ GetResult ⇒ GR }

  /** DDL for all tables. Call .create to execute. */
  lazy val schema: profile.SchemaDescription = Array(
    dummies.schema,
    users.schema,
  ).reduceLeft(_ ++ _)

  lazy val users = new TableQuery(RecordTable(model.users)(_))

  class Dummy(tag: Tag) extends Table[(Int, String)](tag, "dummy") {
    def id = column[Int]("id")
    def email = column[String]("email")
    def * = (id, email)
  }
  lazy val dummies = new TableQuery(new Dummy(_))
}

object Tables extends Tables {
  val profile = slick.jdbc.PostgresProfile
}
