package gv
package record

import
  list._

trait Record
  extends AnyRef
    with StandardNamedTypes
    with evidence.RecordMixIn
    with closed.RecordMixIn
    with untagged.MixIn
{
  type Fields <: List

  def name: String = toString
}

object Record {

  final type of[fields <: List] = Record {
    type Fields = fields
  }

}
