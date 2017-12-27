package gv
package record
package evidence
package find_getter_in

import
  list.op._,
  list._,
  fun._

trait PartialFunction extends Any {

  final implicit def definedFindGetterIn[
    vals <: List,
    n <: Named.of[T],
    T,
  ](
    implicit
    n: n,
    ev: find.Evidence[IsType[n], vals, T]
  ): Defined[FindGetterIn[vals], n, vals ⇒ T] =
    () ⇒ { case `n` ⇒ ev.apply }

}
