package yc
package ph
package service

trait ServicePropertiesProvider extends Any {

  def properties(serviceInstance: adt.ServiceInstance): Option[adt.ServiceProperties]

}