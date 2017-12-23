package gv
package list

trait ToTupleDeco extends Any {

  final implicit def toTupleDeco[T <: List, tuple](_self: T)(implicit ev: ToTuple[T, tuple]): ToTupleOps[T, tuple] =
    new ToTupleOps[T, tuple] {
      val self = _self
      val toTupleEvidence = ev
    }

}
