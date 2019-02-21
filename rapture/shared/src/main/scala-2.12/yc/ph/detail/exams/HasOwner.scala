package yc
package ph
package detail
package exams

import ph.{ service ⇒ outer }

object HasOwner {

  private[this] def zero(comment: String) = adt.ExamReport(comments = Vector(comment))

  private[this] def ten(owner: String) = adt.ExamReport(score = 10, metainformation = Map("owner" → owner))

  private[this] def id(ownerType: String) = adt.ExamId(s"has_${ownerType}_owner")

  def apply(
    propertyProvider: outer.ServicePropertiesProvider,
    getOwner: adt.ServiceProperties ⇒ Option[String],
    ownerType: String,
  ): adt.Exam = {

    def impl(inst: adt.ServiceInstance): adt.ExamReport = {
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
