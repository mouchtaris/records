import sbtcrossproject.{
  crossProject,
  CrossType,
}

lazy val rapture = crossProject(JVMPlatform, JSPlatform)
  .crossType(CrossType.Full)
  // add the `it` configuration
  .configs(IntegrationTest)
  // add `it` tasks
  .settings(Defaults.itSettings: _*)
  // add Scala.js-specific settings and tasks to the `it` configuration
  .jsSettings(inConfig(IntegrationTest)(ScalaJSPlugin.testConfigSettings): _*)
  // add the `shared` folder to source directories
  .settings(
    unmanagedSourceDirectories in IntegrationTest ++=
      CrossType.Full.sharedSrcDir(baseDirectory.value, "it").toSeq
  )
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
//    libraryDependencies += "com.lihaoyi" %%% "scalatags" % "0.6.7",
    //libraryDependencies += "org.scala-js" %% "scalajs-stubs" % scalaJSVersion % "provided",
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
