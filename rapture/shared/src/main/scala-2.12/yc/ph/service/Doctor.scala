package yc
package ph
package service

trait Doctor extends Any {

  def examine(instance: adt.ServiceInstance): adt.ServiceReport

}

