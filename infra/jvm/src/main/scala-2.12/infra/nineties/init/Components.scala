package infra
package nineties
package init

final case class Components(
  config: nineties.config.Config,
  actorSystem: akka.actor.ActorSystem,
  actorMaterializer: akka.stream.ActorMaterializer,
  database: slick.jdbc.JdbcBackend.DatabaseDef
)
