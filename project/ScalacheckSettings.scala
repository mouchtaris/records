import
  sbt._,
  Keys._

object ScalacheckSettings {

  val settings = Seq(
    libraryDependencies ++= Seq(
      "org.scalacheck" %% "scalacheck" % "1.14.0" % "test"
    ),
    (testOptions in Test) +=
      Tests.Argument(
        TestFrameworks.ScalaCheck,
        "-maxSize", "5",
        "-minSuccessfulTests", "33",
        "-workers", "1",
        "-verbosity", "1",
      )
  )

}
