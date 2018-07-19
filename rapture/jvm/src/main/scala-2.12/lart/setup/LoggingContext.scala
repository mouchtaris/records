package lart
package setup

final case class LoggingContext(
  factory: Logging
)

object LoggingContext {
  def apply(akkaContext: AkkaContext): LoggingContext =
    LoggingContext(
      factory = Logging(akkaContext.actorSystem)
    )
}
