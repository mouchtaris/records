package yc
package ph
package detail
package service
package doctor

import ph.{ service ⇒ outer }

final class Fake(val unit: Unit) extends AnyVal with outer.Doctor {

  override def examine(inst: adt.ServiceInstance) = new adt.ServiceReport()

}

object Fake {

  val fake: Fake = new Fake(())

  def apply(): Fake = fake

}
