package xtrm
package model

import list.List

trait RecordEvidence[Fields <: List] extends Any {
  type λ[Vals] <: List
}

object RecordEvidence {
  implicit def nilEvidence: NilRecordEvidence = new NilRecordEvidence(())
  implicit def listEvidence[H, Tail <: List](implicit ev: RecordEvidence[Tail]): ListRecordEvidence[H, Tail, ev.λ] = new ListRecordEvidence(())
}