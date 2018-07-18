import
  sbt._,
  Keys._

object Slf4jSettings {

  val version = "1.2.3"

  val settings = Seq(
    libraryDependencies ++= Seq(
      "ch.qos.logback" % "logback-classic" % version
    )
  )

}

