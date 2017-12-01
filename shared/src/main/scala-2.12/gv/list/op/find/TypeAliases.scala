package gv
package list
package op
package find

trait TypeAliases {

  final type resultOf[pf, list <: List] = {
    type λ[out] = Evidence[pf, list, out]
  }

}
