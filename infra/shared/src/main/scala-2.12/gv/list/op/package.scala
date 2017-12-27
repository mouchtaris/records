package gv
package list

package object op
  extends AnyRef
{

  final implicit class Decor[list <: List](val self: list)
    extends AnyVal
    with find.Decoration[list]
    with map.Decoration[list]
    with select.Decoration[list]
    with get.Decoration[list]

}
