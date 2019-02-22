package yc
package ph
package detail
package service
package properties

import ph.{service ⇒ outer}
import bs.bsjson._

object Fake {

  object ProjectYaml {
    private[this] def dev(value: String) = "lead_developer" → value
    private[this] def own(value: String) = "product_lead" → value

    val platformHealth = JsVal(Map(dev("nikos"), own("fabia")))
    val bobsStuff = JsVal(Map(dev("bob")))
    val wannaBe = JsVal(Map(own("manager")))
  }


  final implicit class Instance(val unit: Unit) extends AnyVal with outer.ComponentPropertyProvider {

    override def properties(componentInstance: adt.ComponentInstance): Option[adt.ComponentProperties] = {
      {
        import ProjectYaml._
        componentInstance.component.projectId.value match {
          case "platform_health" ⇒ Some(platformHealth)
          case "bobs_stuff" ⇒ Some(bobsStuff)
          case "wanna_be" ⇒ Some(wannaBe)
          case _ ⇒ None
        }
      }
        .map(detail.properties.FromProjectYaml.apply)
    }

  }

  val instance: Instance = ()
  def apply(): Instance = instance

}
