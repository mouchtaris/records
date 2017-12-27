package gv
package list

trait ToListOps[T, list <: List] extends Any {

  protected[this] def self: T
  protected[this] implicit def toListEvidence: ToList[T, list]

  final def toList: list = toListEvidence(self)

}
