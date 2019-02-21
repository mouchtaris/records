package yc
package ph
package detail
package exams

import ph.{ service ⇒ outer }

final case class HasOwner(
  propertyProvider: outer.ServicePropertiesProvider,
  getOwner: adt.ServiceProperties ⇒ Option[String],
  ownerType: String,
)
  extends AnyRef
  with adt.Exam
{

  override def id = adt.ExamId(s"has_${ownerType}_owner")

  private[this] def zero(comment: String) = adt.ExamReport(comments = Vector(comment))
  private[this] def ten(owner: String) = adt.ExamReport(score = 10, metainformation = Map("owner" → owner))

  override def apply(inst: adt.ServiceInstance): adt.ExamReport = {
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

}
