package fn

package object reduce
  extends AnyRef
  with pf.Imports
  with list.Imports
{

  final implicit class Decoration[S](override val self: S) extends AnyVal with ReduceOps[S]

}
