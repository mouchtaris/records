package xtrm
package model

import list.List
import get.Get

final class FromRecordEvidenceGetter[T, rev[_] <: List](val unit: Unit) extends AnyVal {

  def apply[V](value: V)(implicit ev: rev[V], get: Get[rev[V], Get[V, T]]): T =
    get(ev)(value)

}


