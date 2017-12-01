import
  sbt._,
  Keys._

object AkkaStreamSettings {

  val settings = Seq(
    libraryDependencies ++= Seq( 
      "com.typesafe.akka" %% "akka-stream" % "2.5.6",
      "com.typesafe.akka" %% "akka-stream-testkit" % "2.5.6" % Test
    )
  )

}
