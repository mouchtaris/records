package gv
package list
package op
package select

trait Decoration[list <: List] extends Any {

  def self: list

  final def select[pf, out <: List](pf: pf)(implicit ev: Evidence[pf, list, out]): out =
    ev(self)

}
