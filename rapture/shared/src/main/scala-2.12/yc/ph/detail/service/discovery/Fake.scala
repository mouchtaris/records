package yc
package ph
package detail
package service
package discovery

import ph.{ service ⇒ outer }

final class Fake(val unit: Unit) extends AnyVal with outer.Discovery {

  override def services: Stream[adt.Service] = {
    Stream(
      adt.Service(projectId = new adt.service.ProjectId(value = "bobs_stuff")),
      adt.Service(projectId = new adt.service.ProjectId(value = "wanna_be")),
      adt.Service(projectId = new adt.service.ProjectId(value = "platform_health")),
    )
  }

}

object Fake {

  val fake: Fake = new Fake(())

  def apply(): Fake = fake

}

