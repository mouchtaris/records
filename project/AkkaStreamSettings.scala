import
  sbt._,
  Keys._

object AkkaStreamSettings {

  import AkkaSettings.{
    AkkaVersion ⇒ Version,
  }

  val settings = Seq(
    libraryDependencies ++= Seq( 
      "com.typesafe.akka" %% "akka-stream" % Version,
      "com.typesafe.akka" %% "akka-stream-testkit" % Version % Test
    )
  )

}
