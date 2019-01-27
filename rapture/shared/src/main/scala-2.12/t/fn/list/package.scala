package t.fn

package object list {

  final type Nil = Nil.type

  final implicit class ListOpsDecoration[S <: List](override val self: S) extends AnyVal with ListOps[S]

}
