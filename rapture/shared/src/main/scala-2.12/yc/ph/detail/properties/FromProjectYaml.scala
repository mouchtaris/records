package yc
package ph
package detail
package properties

import bs.bsjson._

trait FromProjectYaml extends Any with adt.ServiceProperties {

  protected[this] def projectYaml: JsVal

  final def techOwner: Option[String] = {
    projectYaml match {
      case JsMap(map) ⇒ map
        .get(JsVal("lead_developer"))
        .map { case JsStr(owner) ⇒ owner }
      case _ ⇒
        None
    }
  }

  final def productOwner: Option[String] = {
    projectYaml match {
      case JsMap(map) ⇒ map
        .get(JsVal("product_lead"))
        .map { case JsStr(owner) ⇒ owner }
    }
  }

}
