import
  sbt._,
  Keys._

object AkkaHttpSettings {

  val settings = Seq(
    libraryDependencies ++= Seq(
      "com.typesafe.akka" %% "akka-http" % "10.0.10",
      "com.typesafe.akka" %% "akka-http-testkit" % "10.0.10" % Test
    )
  )

}
