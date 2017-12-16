import
  sbt._,
  Keys._

object AkkaActorSettings {

  import AkkaSettings.{
    AkkaVersion ⇒ Version,
  }

  val settings = Seq(
    libraryDependencies ++= Seq(
      "com.typesafe.akka" %% "akka-actor" % Version,
      "com.typesafe.akka" %% "akka-testkit" % Version % Test
    )
  )

}
