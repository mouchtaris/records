package gv
package list

trait Evidence[list <: List, h, t <: List] extends Any {

  final type Head = h

  final type Tail = t

  def apply(list: list): h :: t

}

object Evidence {

  type headOf[list <: List] = { type λ[h] = Evidence[list, h, _ <: List] }

  implicit def listEvidence[h, t <: List]: Evidence[h :: t, h, t] = identity[h :: t]

}


