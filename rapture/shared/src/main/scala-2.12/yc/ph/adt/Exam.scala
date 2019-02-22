package yc
package ph
package adt

final case class Exam(
  id: ExamId,
  impl: ComponentInstance ⇒ ExamReport,
) {

  def apply(inst: ComponentInstance): ExamReport = impl(inst)

}
