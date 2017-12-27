import
  sbt._,
  Keys._,
  org.scalajs.sbtplugin.{
    ScalaJSPlugin,
  },
  org.scalajs.jsdependencies.sbtplugin.{
    JSDependenciesPlugin,
  },
  ScalaJSPlugin.autoImport._,
  JSDependenciesPlugin.autoImport._

object ScalaJsSettings {

  val plugins = Seq(
    ScalaJSPlugin,
    JSDependenciesPlugin,
  )

  val settings = Seq(
    scalaJSUseMainModuleInitializer := false,
  )

}
