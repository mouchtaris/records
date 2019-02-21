package yc
package ph
package detail
package service
package properties

import ph.{service ⇒ outer}
import bs.bsjson._

final class Fake(val unit: Unit)
  extends AnyVal
  with outer.ServicePropertiesProvider
{

  override def properties(serviceInstance: adt.ServiceInstance): Option[adt.ServiceProperties] =
    if (serviceInstance.service.projectId.value == "bobs_stuff")
      Some(new detail.properties.FromProjectYaml {
        def projectYaml: JsVal = JsVal(Map(
          JsVal("lead_developer") → JsVal("rayta")
        ))
      }
    )
    else if (serviceInstance.service.projectId.value == "god")
      Some(new detail.properties.FromProjectYaml {
        def projectYaml: JsVal = JsVal(Map(
          JsVal("product_lead") → JsVal("ivan")
        ))
      })
    else
      None

}

object Fake {

  val instance = new Fake(())

  def apply(): Fake = instance

}