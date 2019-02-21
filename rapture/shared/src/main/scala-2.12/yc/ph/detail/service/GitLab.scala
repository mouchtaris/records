package yc
package ph
package detail
package service

import ph.{service ⇒ outer}

final case class GitLab(
  override val services: Stream[adt.Service],
)
  extends AnyRef
  with outer.Discovery
