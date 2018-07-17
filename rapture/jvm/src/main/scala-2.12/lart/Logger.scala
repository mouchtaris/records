package lart

import hm.TypeInfo.Name
import org.slf4j.{LoggerFactory, Logger ⇒ LoggerImpl}

object Logger {

  final type Logger = LoggerImpl

  def apply[T: Name]: Logger =
    LoggerFactory.getLogger(implicitly[Name[T]].value)

}
