package lr
package adt

import fn._
import reduce._

object Expansion {
  type t[S] = Reduce[S] {
    type State <: Closed[Symbol]
  }
}
