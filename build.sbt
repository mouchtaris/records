import sbtcrossproject.{
    crossProject,
    CrossType,
  }

lazy val infra = crossProject(JVMPlatform, JSPlatform)
  .crossType(CrossType.Full)
  .settings {
    ProjectSettings.settings ++
    ScalacSettings.settings ++
    Seq.empty
  }
  .jvmSettings {
    SlickSettings.settings ++
    AkkaActorSettings.settings ++
    AkkaStreamSettings.settings ++
    AkkaHttpSettings.settings ++
    JRubySettings.settings ++
    ScalaPBSettings.settings ++
    Seq.empty
  }
  .jsSettings {
    ScalaJsSettings.settings ++
    Seq.empty
  }

lazy val infraJVM = infra.jvm

lazy val infraJS = infra.js
  .enablePlugins(ScalaJsSettings.plugins: _*)
