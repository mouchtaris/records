package samo

import to_string._
import list._

package object list_to_string {

  implicit def listToString[
    L,
    H: HeadOf[L]#λ: ToString,
    T: TailOf[L]#λ: ToString,
  ]: ToString[L] =
    list ⇒
      s"${list.head.string} :: ${list.tail.string}"

}
