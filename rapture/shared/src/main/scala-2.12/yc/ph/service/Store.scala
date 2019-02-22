package yc
package ph
package service

trait Store extends Any {

  def store(componentInstance: adt.ComponentInstance, report: adt.ComponentReport): Unit
  def all: Stream[(adt.ComponentInstance, adt.ComponentReport)]

}

