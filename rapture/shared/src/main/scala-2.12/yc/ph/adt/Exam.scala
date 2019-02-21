package yc
package ph
package adt

final case class Exam(
  id: ExamId,
  impl: ServiceInstance ⇒ ExamReport,
) {

  def apply(inst: ServiceInstance): ExamReport = impl(inst)

}
