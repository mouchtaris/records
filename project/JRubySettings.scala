import
  sbt._,
  Keys._

object JRubySettings {

  final val ORG = "org.jruby"
  final val VERSION = "9.1.15.0"

  final object artifacts {
    final val jruby = "jruby"
    final val all = Seq(jruby)
  }

  final val settings = Seq(
    libraryDependencies ++= artifacts.all map (ORG % _ % VERSION)
  )

}

