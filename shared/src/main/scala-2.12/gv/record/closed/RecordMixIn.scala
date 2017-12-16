package gv
package record
package closed

import
  list._

trait RecordMixIn extends AnyRef {
  this: Record ⇒

  object ClosedList extends fun.Named[List]

  sealed trait Closed extends Any {
    def apply[R](f: ClosedList.T ⇒ Evidence[ClosedList.T] ⇒ R): R

    final def get[n <: ScopedNamed[_]](n: n): n.T =
      this { vals ⇒ implicit ev ⇒ vals.got[RecordMixIn.this.type](n) }
  }

  object Closed {

    implicit def apply[vals <: List: Evidence](vals: vals): Closed =
      new Closed {
        val closedList: ClosedList.T =
          vals.asInstanceOf[ClosedList.T]

        val evidence: Evidence[ClosedList.T] =
          implicitly[Evidence[vals]].asInstanceOf[Evidence[ClosedList.T]]

        def apply[R](f: ClosedList.T ⇒ Evidence[ClosedList.T] ⇒ R): R =
          f(closedList)(evidence)
      }

  }

}
