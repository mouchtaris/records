package gv
package list
package op
package map

trait Decoration[list <: List] extends Any {

  def self: list

  final def map[pf, out <: List](pf: pf)(implicit map: Evidence[pf, list, out]): out =
    map(self)

}
