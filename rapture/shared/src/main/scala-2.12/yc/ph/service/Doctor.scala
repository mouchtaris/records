package yc
package ph
package service

trait Doctor extends Any {

  def examine(componentInstance: adt.ComponentInstance): adt.ComponentReport

}

