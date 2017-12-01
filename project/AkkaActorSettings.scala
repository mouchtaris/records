import
  sbt._,
  Keys._

object AkkaActorSettings {

  val settings = Seq(
    libraryDependencies ++= Seq(
      "com.typesafe.akka" %% "akka-actor" % "2.5.6",
      "com.typesafe.akka" %% "akka-testkit" % "2.5.6" % Test
    )
  )

}
