package lart
package setup

final class LoggingContext(
  implicit
  akkaContext: AkkaContext
) {
  val factory: Logging = Logging(akkaContext.actorSystem)
}
