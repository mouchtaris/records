package yc
package ph

import java.nio.file.Files

final case class Facade(
  config: configuration.Configuration,
  discovery: service.Discovery,
  doctor: service.Doctor,
  store: service.Store,
) {

  private[this] def log(level: String, typ: String, details: String): Unit = {
    println(s"""{event: "$typ", severe: "$level", details:  "$details"}""")
  }
  private[this] def serviceDiscovered(s: adt.Service): Unit = {
    log("info", "service_discovered", s"""{projectId: "${s.projectId.value}"}""")
  }
  private[this] def serviceExamined(s: adt.ServiceInstance): Unit = {
    log(level = "info", typ = "service_examined", details = s"""{projectId: "${s.service.projectId.value}", at: "${s.at}"}""")
  }
  private[this] def now: java.time.Instant = {
    java.time.Instant.now()
  }
  private[this] def reportEntries(entries: Stream[(adt.ServiceInstance, adt.ServiceReport)]): Unit = {
    import bs.bsjson.JsVal
    val txt = JsVal(
      entries
        .map { case (instance, report) ⇒ JsVal(instance) → JsVal(report) }
        .toVector
    )
    val outpath = java.nio.file.Paths.get("./test.rb")
    val lines = Vector(
      "require 'pp'",
      "pp(",
      txt.toString,
      ")",
    )
    val jlines: java.lang.Iterable[CharSequence] = scala.collection.JavaConverters.asJavaIterable(lines)
    Files.write(outpath, jlines)
    println(lines.mkString("\n"))
  }

  def analyse(): Unit = {
    discovery.services.foreach { service ⇒
      serviceDiscovered(service)

      val instance = adt.ServiceInstance(service, at = now, commit = adt.CommitId.random)
      val report = doctor.examine(instance)
      serviceExamined(instance)

      store.store(instance, report)
    }
  }

  def report(): Unit = {
    reportEntries(store.all)
  }

  def run(): Unit = {
    analyse()
    report()
  }

}
