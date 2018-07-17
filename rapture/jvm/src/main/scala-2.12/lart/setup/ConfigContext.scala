package lart.setup

import hm.config.Config
import hm.{ config ⇒ ConfigFactory }

final case class ConfigContext(
  config: Config
)

object ConfigContext {
  def apply(): ConfigContext = ConfigContext(
    config = ConfigFactory()
  )
}
