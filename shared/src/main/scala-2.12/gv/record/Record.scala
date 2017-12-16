package gv
package record

import
  fun._,
  list._

trait Record
  extends AnyRef
    with StandardNamedTypes
    with evidence.RecordMixIn
    with closed.RecordMixIn
{
  type Fields <: List
}

object Record {

  final type of[fields <: List] = Record {
    type Fields = fields
  }

}
