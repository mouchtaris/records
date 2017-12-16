package gv
package record

import
  list._

final class Concatenation[r1 <: Record, r2 <: Record, fields <: List](
  val r1: r1,
  val r2: r2,
)
  extends AnyRef
    with Record
{
  type Fields = fields

  implicit def implicitEvidenceForRecord1[vals <: List: Evidence]: r1.Evidence[vals] =
    implicitly[Evidence[vals]].asInstanceOf[r1.Evidence[vals]]

  implicit def implicitEvidenceForRecord2[vals <: List: Evidence]: r2.Evidence[vals] =
    implicitly[Evidence[vals]].asInstanceOf[r2.Evidence[vals]]
}

