package yc
package ph
package service

trait ServiceProperties extends Any {

  def properties(service: adt.Service, at: java.time.Instant): Option[ServiceProperties]

}