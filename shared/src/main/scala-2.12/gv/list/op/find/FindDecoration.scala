package gv
package list
package op
package find

trait FindDecoration[list <: List] extends Any {

  def self: list

  final def find[pf, out](pf: pf)(implicit find: Evidence[pf, list, out]): out = find(self)

}
