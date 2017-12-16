package gv
package list
package op
package concat

trait TypeAliases extends Any {

  sealed trait resultOf[l1 <: List, l2 <: List] {
    final type λ[out <: List] = Evidence[l1, l2, out]
  }

}
