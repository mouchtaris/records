package yc
package ph
package service

trait Discovery extends Any {

  def services: Stream[adt.Service]

}
