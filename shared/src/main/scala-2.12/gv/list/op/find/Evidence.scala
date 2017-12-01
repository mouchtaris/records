package gv
package list
package op
package find

trait Evidence[pf, list <: List, out] {

  def apply(list: list): out

}
