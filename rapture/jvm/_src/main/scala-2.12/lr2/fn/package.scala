package lr2

import scala.annotation.tailrec

package object fn {

  @tailrec
  final def reduce[S](zero: S)(next: S ⇒ Option[S]): S =
    next(zero) match {
      case Some(one) ⇒
        reduce(one)(next)
      case None ⇒
        zero
    }

}
