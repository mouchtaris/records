package gv
package list
package op
package find

trait Deductions extends Any {

  final implicit def findFromSelectEvidence[pf, list <: List, lout <: List, out](
    implicit
    select: Select[pf, list, lout],
    listEv: list.Evidence[lout, out, _ <: List]
  ): Evidence[pf, list, out] =
    list ⇒ listEv(select(list)).head

}
