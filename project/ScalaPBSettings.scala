import
  sbt._,
  Keys._,
  sbtprotoc.ProtocPlugin.autoImport._

object ScalaPBSettings {

  val pbTarget = PB.targets in Compile := Seq(
    scalapb.gen() → sourceManaged.in(Compile).value
  )

  val pbSources = PB.protoSources in Compile := Seq(
    file("rapture/shared/src/main/protobuf")
  )

  val settings = Seq(
    pbTarget,
    pbSources,
  )

}

