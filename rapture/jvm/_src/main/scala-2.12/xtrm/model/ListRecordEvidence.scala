package xtrm
package model

import list.{List, ::}
import get.Get

final class ListRecordEvidence[H, Tail <: List, TailEvidenceT[_] <: List](val unit: Unit)
  extends AnyVal
  with RecordEvidence[H :: Tail]
{
  type λ[Vals] = Get[Vals, H] :: TailEvidenceT[Vals]
}

