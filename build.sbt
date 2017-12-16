lazy val root = project.in(file("."))
  .aggregate(musaeJVM, musaeJS)
  .settings(
    publish := {},
    publishLocal := {},
  )

lazy val musae = crossProject.in(file("."))
  .settings(ProjectSettings.settings)
  .settings(ScalacSettings.settings)
  .jvmSettings(SlickSettings.settings)
  .jvmSettings(AkkaActorSettings.settings)
  .jvmSettings(AkkaStreamSettings.settings)
  .jvmSettings(AkkaHttpSettings.settings)
  .jvmSettings(JRubySettings.settings)
  .jsSettings(ScalaJsSettings.settings)

lazy val musaeJVM = musae.jvm

lazy val musaeJS = musae.js
  .enablePlugins(
    ScalaJsSettings.plugin
  )
