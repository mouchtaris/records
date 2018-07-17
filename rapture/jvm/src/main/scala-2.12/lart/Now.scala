package lart

import scala.concurrent.ExecutionContext

object Now extends ExecutionContext {
  def execute(r: Runnable): Unit =
    r.run()
  def reportFailure(t: Throwable): Unit =
    throw t
}

