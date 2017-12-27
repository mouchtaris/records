package gv
package config

object DatabaseConfig
  extends AnyRef
    with database_config.Record
{

  final val fromUri = new database_config.FromDatabaseConfigConstructor { }.create
  final val fromConfig = new database_config.FromDatabaseConfigConstructor { }.create

  final implicit class ClosedDeco(val self: Closed)
    extends AnyRef
      with database_config.ClosedOps
}
