package yc
package ph
package detail
package service
package browsing

import java.time.Instant

import ph.{service ⇒ outer}

final class Fake(val unit: Unit) extends AnyVal with outer.Browsing {

  override def components: Stream[adt.Component] = {
    Stream(
      adt.Component(projectId = adt.component.ProjectId(value = "bobs_stuff")),
      adt.Component(projectId = adt.component.ProjectId(value = "wanna_be")),
      adt.Component(projectId = adt.component.ProjectId(value = "platform_health")),
      adt.Component(projectId = adt.component.ProjectId(value = "abandoned")),
    )
  }

  override def latest(component: adt.Component): Option[adt.ComponentInstance] = {
    Fake.instances.find(_.component == component)
  }

}

object Fake {

  private[this] def makeInstance(name: String, at: java.time.Instant) =
    adt.ComponentInstance(
      component = adt.Component(projectId = adt.component.ProjectId(value = name)),
      at = at,
      commit = adt.GitRef.head("master")
    )

  private[this] def now(minusDays: Int): Instant = Instant.now()
    .minusSeconds(60 * 60 * 24 * minusDays)

  val instances: Vector[adt.ComponentInstance] = Vector(
    makeInstance(name = "bobs_stuff", now(20)), // 20 days ago
    makeInstance(name = "wanna_be", now(10)),
    makeInstance(name = "platform_health", now(1)),
    makeInstance(name = "abandoned", now(50)),
  )

  val fake: Fake = new Fake(())

  def apply(): Fake = fake

}
