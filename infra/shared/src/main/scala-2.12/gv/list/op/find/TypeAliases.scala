package gv
package list
package op
package find

trait TypeAliases extends Any {

  sealed trait resultOf[pf, list <: List] {
    final type λ[out] = Evidence[pf, list, out]
  }

}
