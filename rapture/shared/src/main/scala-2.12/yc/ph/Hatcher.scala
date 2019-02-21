package yc
package ph

import java.net.URI

object Hatcher {

  val fake = Facade(
    config = ph.detail.configuration.Fake.fake,
    discovery = ph.detail.service.discovery.Fake.fake,
    doctor = ph.detail.service.doctor.OfExamination(
      adt.Examination(
        ph.detail.exams.Bollock.instance,
        ph.detail.exams.Bastard.instance,
      )
    ),
    store = ph.detail.service.store.Memory(),
  )

  def main(args: Array[String]): Unit = {
    fake.run()
  }

}
