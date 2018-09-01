package samo
package list_case

import list._

trait Cons {
  def :::[H](head: H): H ::: this.type = new :::(_head = head, _tail = this)
}


