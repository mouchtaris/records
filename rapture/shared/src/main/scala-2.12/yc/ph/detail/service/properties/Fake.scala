package yc
package ph
package detail
package service
package properties

import ph.{service ⇒ outer}

final class Fake(val unit: Unit)
  extends AnyVal
  with outer.ServicePropertiesProvider
{

  override def properties(serviceInstance: adt.ServiceInstance): Option[adt.ServiceProperties] =
    if (serviceInstance.service.projectId.value == "bobs_stuff")
      Some(new EmptyProperties {
        override val techOwner: Option[String] = Some("bob")
      })
    else if (serviceInstance.service.projectId.value == "god")
      Some(new EmptyProperties {
        override val productOwner: Option[String] = Some("gad")
      })
    else
      None

}

object Fake {

  val instance = new Fake(())

  def apply(): Fake = instance

}