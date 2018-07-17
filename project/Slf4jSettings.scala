import
  sbt._,
  Keys._

object Slf4jSettings {

  val version = "1.0.13"

  val settings = Seq(
    libraryDependencies ++= Seq(
      "ch.qos.logback" % "logback-classic" % version
    )
  )

}

