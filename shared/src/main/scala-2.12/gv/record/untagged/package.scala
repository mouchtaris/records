package gv
package record

import
  list._,
  list.op._,
  fun._,
  tag._

package object untagged {

  trait FromUntagged[
    fields <: List,
    vals <: List,
    out <: List,
  ]
    extends Any
  {

    def apply(vals: vals): out

  }

  implicit val nilFromUntagged: FromUntagged[Nil, Nil, Nil] = identity[Nil]

  implicit def listFromUntagged[
    n <: Named[b],
    fields <: List,
    b,
    vals <: List,
    tout <: List
  ](
    implicit
    n: n,
    t: FromUntagged[fields, vals, tout],
  ): FromUntagged[n :: fields, b :: vals, n.T :: tout] =
    (vals: b :: vals) ⇒
      n(vals.head) :: t(vals.tail)

  type ToUntagged[vals <: List, untagged <: List] = map.Evidence[Unname, vals, untagged]
  def toUntagged[vals <: List, untagged <: List](vals: vals)(implicit ev: ToUntagged[vals, untagged]): untagged = ev(vals)

  trait MixIn {
    this: Record ⇒

    final def fromUntagged[
      vals <: List,
      tagged <: List,
    ](
      vals: vals,
    )(
      implicit
      ev: FromUntagged[Fields, vals, tagged],
    ): tagged =
      ev(vals)

  }
}
