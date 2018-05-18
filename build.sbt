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
lazy val raptureJVM = rapture.jvm
lazy val raptureJS = rapture.js
