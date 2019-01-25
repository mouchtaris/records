package fn

import
  list._,
  scala.annotation.tailrec

object reduce {

  @tailrec
  def apply[S](zero: S)(next: S ⇒ Option[S]): S =
    next(zero) match {
      case Some(one) ⇒ reduce(one)(next)
      case None ⇒ zero
    }

  trait Reduce[S] extends Any with TypeClass[S] {
    type State

    final type Next =
      State ⇒ Option[State]

    def zero: R[State]

    def next: R[Next]
  }

}
