package lart

import akka.actor.ActorSystem
import akka.event.{Logging ⇒ AkkaLogging, LoggingAdapter}
import hm.TypeInfo.Name

object Logging {
  type Logger = LoggingAdapter

  implicit class LoggerDecorations(val value: Logger) extends AnyVal {
    def warn(fmt: String, args: Any*): Unit = value.log(AkkaLogging.WarningLevel, value.format(fmt, args: _*))
  }
}

final case class Logging(
  actorSystem: ActorSystem,
) {
  import Logging._

  def apply[T: Name]: Logger =
    AkkaLogging(actorSystem, implicitly[Name[T]].value)

}
