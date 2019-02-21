package yc
package ph
package detail
package service
package store

import ph.{ service ⇒ outer }
import memory.{ Repo, Repos }

final class Memory(val repos: Repos) extends AnyVal with outer.Store {

  private[this] def _reports: Repo = repos._2

  override def store(instance: adt.ServiceInstance, report: adt.ServiceReport): Unit = {
    _reports += (instance → report)
  }

  override def all: Stream[(adt.ServiceInstance, adt.ServiceReport)] = {
    _reports.toStream
  }

}

object Memory {

  def apply(): Memory = new Memory(memory.repos)

}
