import
  sbt._,
  Keys._

object ArgonaughtSetting {
  
  val settings = Seq(
    libraryDependencies ++= Seq(
      "io.argonaut" %% "argonaut" % "6.2.2" changing()  
    )
  )
}
