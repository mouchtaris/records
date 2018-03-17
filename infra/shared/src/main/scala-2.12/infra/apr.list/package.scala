package infra
package apr

package object list {

  final implicit class ListDeco[s <: List](val self: s)
    extends AnyVal
    with ListOps[s]

}
