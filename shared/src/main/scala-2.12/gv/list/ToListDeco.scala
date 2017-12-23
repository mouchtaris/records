package gv
package list

trait ToListDeco extends Any {

  final implicit def toListDeco[T, list <: List](_self: T)(implicit tl: ToList[T, list]): ToListOps[T, list] =
    new ToListOps[T, list] {
      val self = _self
      val toListEvidence = tl
    }

}