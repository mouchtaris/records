import
  sbt._,
  Keys._

object SlickSettings {

  val version = "3.2.3"

  val settings = Seq(
    libraryDependencies ++= Seq(
      "com.typesafe.slick" %% "slick" % version,
      "com.typesafe.slick" %% "slick-hikaricp" % version,
      "com.typesafe.slick" %% "slick-codegen" % version,
      "org.slf4j" % "slf4j-nop" % "1.6.4",
      "org.postgresql" % "postgresql" % "42.1.4"
    )
  )

}

