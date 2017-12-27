package gv
package list

trait ToTupleOps[self <: List, tuple] extends Any {

  protected[this] def self: self
  protected[this] implicit def toTupleEvidence: ToTuple[self, tuple]

  final def toTuple: tuple =
    toTupleEvidence(self)

}
