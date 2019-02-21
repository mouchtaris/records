package yc
package ph
package detail
package exams

final class Bollock(val unit: Unit)
  extends AnyVal
  with adt.Exam
{

  override def id = adt.ExamId("Bollockness")

  override def apply(inst: adt.ServiceInstance) = adt.ExamReport(
    score = 10,
  )

}

object Bollock {

  val instance = new Bollock(())

  def apply(): Bollock = instance

}
