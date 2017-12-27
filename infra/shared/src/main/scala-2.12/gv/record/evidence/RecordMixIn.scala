package gv
package record
package evidence

import
  list._

trait RecordMixIn extends Any {
  this: Record ⇒

  final type Evidence[vals <: List] = `for`[Fields]#λ[vals]
}
