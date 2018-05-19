import sbtcrossproject.{
  crossProject,
  CrossType,
}

lazy val rapture = crossProject(JVMPlatform, JSPlatform)
    .crossType(CrossType.Full)
    .settings {
      ProjectSettings.settings ++
      ScalacSettings.settings ++
      Seq.empty
    }
  .jvmSettings(
    GoogleSettings.settings ++
    AkkaActorSettings.settings ++
    AkkaHttpSettings.settings ++
    AkkaStreamSettings.settings ++
    JRubySettings.settings ++
    Seq.empty
  )
lazy val raptureJVM = rapture.jvm
lazy val raptureJS = rapture.js
