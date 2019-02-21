package yc
package ph
package detail
package exams

final class Bastard(val unit: Unit)
  extends AnyVal
  with adt.Exam
{

  override def id = adt.ExamId("Bastardness")

  override def apply(inst: adt.ServiceInstance) = adt.ExamReport(
    score = new java.util.Random().nextLong(),
    comments = Vector(
      "This is a random generated value",
    )
  )

}

object Bastard {

  val instance: Bastard = new Bastard(())

  def apply(): Bastard = instance

}
