package gv
package list
package op
package find

trait Evidence[pf, list <: List, out] extends Any {

  def apply(list: list): out

}