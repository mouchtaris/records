package gv
package list
package op
package select

trait Evidence[pf, list <: List, out <: List] extends Any {

  def apply(list: list): out

}

