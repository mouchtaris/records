package yc
package ph
package adt

final case class ComponentReport(
  exams: ComponentReport.Exams,
) {

  val overall: Long = exams.map(_._2.score).sum

  val possibleMax: Long = exams.map(_._2.max).sum

}

object ComponentReport {

  type Exams = Map[ExamId, ExamReport]

  val emptyExams: Exams = Map.empty

  val empty = ComponentReport(emptyExams)

}
