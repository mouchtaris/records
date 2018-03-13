package gv

import
  scala.util.{
    Try,
  },
  scala.concurrent.{
    Future,
  }

object control {
  import scala.util.control.Exception._

  final implicit class As[R](val self: () ⇒ R) extends AnyVal {
    private[this] def katsch: Catch[R] = nonFatalCatch
    def tri: Try[R] = katsch withTry self()
    def future: Future[R] = Future fromTry tri
  }

  def apply[R](b: ⇒ R): As[R] = () ⇒ b
}


