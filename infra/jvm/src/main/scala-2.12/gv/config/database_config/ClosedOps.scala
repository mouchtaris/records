package gv
package config
package database_config

trait ClosedOps extends Any {
  import config.{ DatabaseConfig ⇒ r }

  def self: r.Closed

  final def driver: r.driver.T = self get r.driver
  final def profile: r.profile.T = self get r.profile
  final def host: r.host.T = self get r.host
  final def port: r.port.T = self get r.port
  final def user: r.user.T = self get r.user
  final def password: r.password.T = self get r.password

}
