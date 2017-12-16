import
  sbt._,
  Keys._

object AkkaHttpSettings {

  import AkkaSettings.{
    AkkaHttpVersion ⇒ Version,
  }

  val settings = Seq(
    libraryDependencies ++= Seq(
      "com.typesafe.akka" %% "akka-http" % Version,
      "com.typesafe.akka" %% "akka-http-testkit" % Version % Test
    )
  )

}
