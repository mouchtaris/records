package gv
package list
package op
package contains

trait TypeAliases extends Any {

  sealed trait in[list <: List] {
    final type λ[t] = Evidence[t, list]
  }

}
