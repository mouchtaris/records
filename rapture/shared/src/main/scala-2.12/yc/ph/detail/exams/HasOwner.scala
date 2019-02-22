package yc
package ph
package detail
package exams

import ph.{ service ⇒ outer }

object HasOwner {

  val MAX_SCORE = 10

  private[this] def zero(comment: String) = adt.ExamReport(score = 0, max = MAX_SCORE, comments = Vector(comment))

  private[this] def ten(owner: String) = adt.ExamReport(score = MAX_SCORE, max = MAX_SCORE, metainformation = Map("owner" → owner))

  private[this] def id(ownerType: String) = adt.ExamId(s"has_${ownerType}_owner")

  def apply(
    propertyProvider: outer.ComponentPropertyProvider,
    getOwner: adt.ComponentProperties ⇒ Option[String],
    ownerType: String,
  ): adt.Exam = {

    def impl(inst: adt.ComponentInstance): adt.ExamReport = {
      propertyProvider.properties(inst) match {
        case Some(props) ⇒
          getOwner(props) match {
            case Some(owner) ⇒ ten(owner)
            case None ⇒ zero("No owner found")
          }
        case None ⇒
          zero("No properties found")
      }
    }

    adt.Exam(id(ownerType), impl)
  }

}
