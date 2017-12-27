package gv
package record

import
  list._

trait Evidence[
  fields <: List,
  vals <: List,
]
  extends Any
{
  def getters: evidence.Getters[fields]#λ[vals]
}
