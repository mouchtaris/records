package yc
package ph
package detail
package service
package discovery

import ph.{ service ⇒ outer }

final class Fake(val unit: Unit) extends AnyVal with outer.Discovery {

  override def services: Stream[adt.Service] = {
    Stream(
      adt.Service(projectId = new adt.service.ProjectId(value = "backend")),
      adt.Service(projectId = new adt.service.ProjectId(value = "frontend")),
    )
  }

}

object Fake {

  val fake: Fake = new Fake(())

  def apply(): Fake = fake

}

