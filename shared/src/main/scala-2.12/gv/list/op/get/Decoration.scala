package gv
package list
package op
package get

import
  fun._

trait Decoration[list <: List] extends Any {

  def self: list

  final def get[t <: Type](t: t)(
    implicit
    ev: Evidence[t, list],
    itsok: t#T <:< t.T
  ): t.T =
    itsok(ev(self))

}
