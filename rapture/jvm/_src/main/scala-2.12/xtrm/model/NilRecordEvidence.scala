package xtrm
package model

import list.Nil

final class NilRecordEvidence(val unit: Unit)
  extends AnyVal
  with RecordEvidence[Nil]
{
  type λ[Vals] = Nil
}
