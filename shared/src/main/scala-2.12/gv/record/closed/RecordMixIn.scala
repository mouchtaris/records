package gv
package record
package closed

import
  list._

trait RecordMixIn extends AnyRef {
  this: Record ⇒

  object ClosedList extends fun.Named[List]

  sealed trait Closed extends Any {
    protected[this] def closedList: ClosedList.T
    protected[this] def evidence: Evidence[ClosedList.T]

    final def asClosedList(vals: List): ClosedList.T =
      vals.asInstanceOf[ClosedList.T]
    final def asClosedEvidence[vals <: List: Evidence]: Evidence[ClosedList.T] =
      implicitly[Evidence[vals]].asInstanceOf[Evidence[ClosedList.T]]

    final def apply[R](f: ClosedList.T ⇒ Evidence[ClosedList.T] ⇒ R): R =
      f(closedList)(evidence)

    final def get[n <: ScopedNamed[_]](n: n): n.T =
      this { vals ⇒ implicit ev ⇒ vals.got[RecordMixIn.this.type](n) }
  }

  object Closed {

    implicit def apply[vals <: List: Evidence](vals: vals): Closed =
      new Closed {
        val closedList = asClosedList(vals)
        val evidence = asClosedEvidence[vals]
      }

  }

}
