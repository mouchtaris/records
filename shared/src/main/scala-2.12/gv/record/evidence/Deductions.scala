package gv
package record
package evidence

import
  list._

object Deductions {

  final implicit class GhostlyEvidence[
    fields <: List,
    vals <: List,
  ](
    val getters: Getters[fields]#λ[vals]
  )
    extends AnyVal
      with record.Evidence[fields, vals]

}

trait Deductions extends Any {

  final implicit def implicitRecordEvidence[
    fields <: List,
    vals <: List : Getters[fields]#λ,
  ]: Deductions.GhostlyEvidence[fields, vals] =
    implicitly[Getters[fields]#λ[vals]]

}
