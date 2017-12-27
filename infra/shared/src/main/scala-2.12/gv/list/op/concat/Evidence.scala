package gv
package list
package op
package concat

trait Evidence[l1 <: List, l2 <: List, out <: List] extends Any {

  def apply(l1: l1, l2: l2): out

}

