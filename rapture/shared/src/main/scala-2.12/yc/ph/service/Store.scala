package yc
package ph
package service

trait Store extends Any {

  def store(serviceInstance: adt.ServiceInstance, report: adt.ServiceReport): Unit
  def all: Stream[(adt.ServiceInstance, adt.ServiceReport)]

}

