package yc
package ph

object Hatcher {

  val propertyProvider: service.ComponentPropertyProvider =
    detail.service.properties.Fake.instance

  val fake = Facade(
    config = detail.configuration.Fake.fake,
    browsing = detail.service.browsing.Fake.fake,
    doctor = detail.service.doctor.OfExamination(
      adt.Examination(
        detail.exams.HasOwner(propertyProvider, _.techOwner, "tech"),
        detail.exams.HasOwner(propertyProvider, _.productOwner, "product"),
      )
    ),
    store = ph.detail.service.store.Memory(),
  )

  def main(args: Array[String]): Unit = {
    fake.run()
  }

}
