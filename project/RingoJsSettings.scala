import
  sbt._,
  Keys._

object RingoJsSettings {

  val settings = Seq(
    libraryDependencies ++= Seq(
      "org.ringojs" % "ringojs" % "1.1.0",
    )
  )

}
