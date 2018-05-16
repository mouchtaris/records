import
  sbt._,
  Keys._

object ProjectSettings {

  val Name = "rapture"
  val Version = "0.0.1-SNAPSHOT"

  val settings = Seq(
    name := Name,
    version := Version,
  )

}

