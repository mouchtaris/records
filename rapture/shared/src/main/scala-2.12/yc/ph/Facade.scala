package yc
package ph

import java.nio.file.Files

final case class Facade(
  config: configuration.Configuration,
  browsing: service.Browsing,
  doctor: service.Doctor,
  store: service.Store,
) {

  private[this] def log(level: String, typ: String, details: String): Unit = {
    println(s"""{event: "$typ", severe: "$level", details:  "$details"}""")
  }
  private[this] def componentDiscovered(s: adt.Component): Unit = {
    log("info", "component_discovered", s"""{projectId: "${s.projectId.value}"}""")
  }
  private[this] def componentExamined(s: adt.ComponentInstance): Unit = {
    log(level = "info", typ = "component_examined", details = s"""{projectId: "${s.component.projectId.value}", at: "${s.at}"}""")
  }
  private[this] def now: java.time.Instant = {
    java.time.Instant.now()
  }
  private[this] def reportEntries(entries: Stream[(adt.ComponentInstance, adt.ComponentReport)]): Unit = {
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
    browsing.components.foreach { component ⇒
      componentDiscovered(component)

      browsing.latest(component).foreach { instance ⇒
        val report = doctor.examine(instance)
        componentExamined(instance)

        store.store(instance, report)
      }
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
