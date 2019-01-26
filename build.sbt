import sbtcrossproject.{
  crossProject,
  CrossType,
}

lazy val rapture = crossProject(JVMPlatform, JSPlatform)
  .crossType(CrossType.Full)
  .settings {
    ProjectSettings.settings ++
    ScalacSettings.settings ++
    ScalaPBSettings.settings ++
    ScalacheckSettings.settings ++
    Seq.empty
  }
  .jsSettings(
    scalaJSUseMainModuleInitializer := true,
    mainClass in Compile := Some("hatchjs.Main"),
    libraryDependencies += "org.scala-js" %%% "scalajs-dom" % "0.9.6",
  )
  .jvmSettings(
    GoogleSettings.settings ++
    AkkaActorSettings.settings ++
    AkkaHttpSettings.settings ++
    AkkaStreamSettings.settings ++
    JRubySettings.settings ++
    SlickSettings.settings ++
    Slf4jSettings.settings ++
    ArgonaughtSetting.settings ++
    CirceSettings.settings ++
    RingoJsSettings.settings ++
    Seq.empty
  )
lazy val raptureJVM = rapture.jvm
lazy val raptureJS = rapture.js
