package gv
package fun

import
  tag._,
  lang._

trait Named[base]
  extends AnyRef
    with Tagverse
    with ImplicitlyAvailable
    with fun.Type
{
  final type Base = base

  final type T = Tagged[base]
}

object Named {

  type of[t] = Named[_] { type T = t }

}
