import sbt.{Def, _}
import Keys._

object GoogleSettings {
  val Version = "1.23.0"
  val Artifacts = Seq(
    "com.google.api-client" → Seq(
      "google-api-client",
      "google-api-client-gson",
      "google-api-client-jackson2",
      "google-api-client-java6",
      "google-api-client-protobuf",
      "google-api-client-xml",
    ),
    "com.google.http-client" → Seq(
      "google-http-client",
      "google-http-client-xml",
      "google-http-client-protobuf",
      "google-http-client-jdo",
      "google-http-client-jackson2",
      "google-http-client-gson"
    ),
    "com.google.oauth-client" → Seq(
      "google-oauth-client",
      "google-oauth-client-java6",
    )
  )

  val dependencies: Seq[ModuleID] =
    Artifacts
      .flatMap { case (org, ids) ⇒
        ids.map { id ⇒
          org % id % Version
        }
      }

  val settings: Seq[Def.Setting[Seq[ModuleID]]] = Seq(
    libraryDependencies ++= dependencies
  )

}
