package yc
package ph
package detail
package service
package doctor

import ph.{ service ⇒ outer }

final case class OfExamination(
  examination: adt.Examination,
)
  extends AnyRef
  with outer.Doctor {

  override def examine(instance: adt.ComponentInstance) = adt.ComponentReport(
    exams = examination
      .exams
      .foldLeft(adt.ComponentReport.emptyExams) { (value, exam) ⇒
        val report: adt.ExamReport = exam(instance)
        value + (exam.id → report)
      }
  )

}
