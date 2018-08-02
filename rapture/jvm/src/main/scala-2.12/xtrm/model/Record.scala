package xtrm
package model

import list.List

final class Record[REV[_] <: List](val unit: Unit) extends AnyVal
{
  type e[Vals] = REV[Vals]
  def get[T] = new FromRecordEvidenceGetter[T, e](())
}

object Record {
  def apply[Fields <: List](implicit ev: RecordEvidence[Fields]): Record[ev.λ] = new Record(())
}

