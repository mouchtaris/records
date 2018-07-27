import
  sbt._,
  Keys._

object ScalacheckSettings {

  val settings = Seq(
    libraryDependencies ++= Seq(
      "org.scalacheck" %% "scalacheck" % "1.14.0" % "test"
    )
  )

}
