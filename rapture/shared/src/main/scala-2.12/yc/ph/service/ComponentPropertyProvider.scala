package yc
package ph
package service

trait ComponentPropertyProvider extends Any {

  def properties(componentInstance: adt.ComponentInstance): Option[adt.ComponentProperties]

}