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
  with outer.Doctor
{

  override def examine(instance: adt.ServiceInstance) = adt.ServiceReport {
    examination
      .exams
      .foldLeft(adt.ServiceReport.Value.empty) { (value, exam) ⇒
        val report: adt.ExamReport = exam(instance)
        value + (exam.id → report)
      }
  }

}
