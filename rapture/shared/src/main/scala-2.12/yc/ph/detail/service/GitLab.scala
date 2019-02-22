package yc
package ph
package detail
package service

import ph.{service ⇒ outer}

case class GitLab(
  override val components: Stream[adt.Component],
)
  extends AnyRef
  with outer.Browsing
  with outer.ComponentPropertyProvider
{

  override def latest(component: adt.Component): Option[adt.ComponentInstance] = {
    ???
  }

  override def properties(componentInstance: adt.ComponentInstance): Option[adt.ComponentProperties] = {
    ???
  }

}