import
  sbt._,
  Keys._,
  org.scalajs.sbtplugin.ScalaJSPlugin,
  ScalaJSPlugin.autoImport._

object ScalaJsSettings {

  val settings = Seq(
    scalaJSUseMainModuleInitializer := true,
    libraryDependencies += "org.scala-js" %%% "scalajs-dom" % "0.9.1"
  )

  val plugin = ScalaJSPlugin

}
