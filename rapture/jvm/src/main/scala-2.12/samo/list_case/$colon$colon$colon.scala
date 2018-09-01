package samo
package list_case

import list._

final case class :::[H, T](_head: H, _tail: T) extends AnyRef with Cons

object ::: {
  implicit def head[H, T]: (H ::: T) Head H = _._head
  implicit def tail[H, T]: (H ::: T) Tail T = _._tail
}
