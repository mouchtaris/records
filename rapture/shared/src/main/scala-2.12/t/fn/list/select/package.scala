package t.fn
package list

package object select
  extends AnyRef
  with pf.Imports {

  final implicit class Decoration[S](override val self: S) extends AnyVal with SelectOps[S]

}
