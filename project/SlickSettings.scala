import
  sbt._,
  Keys._

object SlickSettings {

  val settings = Seq(
    libraryDependencies ++= Seq(
      "com.typesafe.slick" %% "slick" % "3.2.2",
      "com.typesafe.slick" %% "slick-hikaricp" % "3.2.2",
      "com.typesafe.slick" %% "slick-codegen" % "3.2.2",
      "org.slf4j" % "slf4j-nop" % "1.6.4",
      "org.postgresql" % "postgresql" % "42.1.4"
    )
  )

}
