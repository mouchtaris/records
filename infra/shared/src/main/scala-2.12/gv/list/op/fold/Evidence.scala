package gv
package list
package op
package fold

trait Evidence[pf, list <: List, in, out] extends Any {

  def apply(in: in): out

}

