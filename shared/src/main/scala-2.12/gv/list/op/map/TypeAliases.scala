package gv
package list
package op
package map

trait TypeAliases extends Any {

  sealed trait resultOf[pf, list <: List] {
    final type λ[out <: List] = Evidence[pf, list, out]
  }

}
