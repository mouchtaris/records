import
  sbt._,
  Keys._

object CirceSettings {

  val version = "0.10.0"

  val settings = Seq(
    libraryDependencies ++= Seq(
      "io.circe" %% "circe-core",
      "io.circe" %% "circe-generic",
      "io.circe" %% "circe-parser",
      "io.circe" %% "circe-optics",
    ).map(_ % version )
  )

}
