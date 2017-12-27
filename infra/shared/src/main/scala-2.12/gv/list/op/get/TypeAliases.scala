package gv
package list
package op
package get

import
  fun._

trait TypeAliases extends Any {

  sealed trait gettableFrom[list <: List] {
    final type λ[t <: Type] = Evidence[t, list]
  }

}
