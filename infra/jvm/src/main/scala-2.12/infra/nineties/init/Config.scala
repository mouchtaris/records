package infra
package nineties
package init

import
  com.typesafe.config.{
    ConfigFactory,
  }

final case class Config(
)
  extends Init[config.Config]
{

  def initialize(): Result =
    Future successful config.Config(ConfigFactory.defaultApplication())

}
