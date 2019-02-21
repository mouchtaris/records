package yc
package ph
package detail
package properties

import bs.bsjson._

object FromProjectYaml {

  def apply(projectYaml: JsVal): adt.ServiceProperties = {

    val techOwner: Option[String] = {
      projectYaml match {
        case JsMap(map) ⇒ map
          .get(JsVal("lead_developer"))
          .map { case JsStr(owner) ⇒ owner }
        case _ ⇒
          None
      }
    }

    val productOwner: Option[String] = {
      projectYaml match {
        case JsMap(map) ⇒ map
          .get(JsVal("product_lead"))
          .map { case JsStr(owner) ⇒ owner }
      }
    }

    adt.ServiceProperties(
      techOwner = techOwner,
      productOwner = productOwner,
    )
  }

}
