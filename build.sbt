lazy val musae = crossProject.in(file("."))
  .settings(ProjectSettings.settings)
  .settings(ScalacSettings.settings)
  .enablePlugins(
    ScalaJsSettings.plugin
  )
  .settings(ScalaJsSettings.settings)
  .settings(SlickSettings.settings)
  .settings(AkkaActorSettings.settings)
  .settings(AkkaStreamSettings.settings)
  .settings(AkkaHttpSettings.settings)

lazy val musaeJVM = musae.jvm
lazy val musaeJS = musae.js
