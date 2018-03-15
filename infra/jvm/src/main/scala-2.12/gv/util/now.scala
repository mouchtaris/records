package gv
package util

import
  scala.concurrent.{
    ExecutionContext,
  }

object now
  extends AnyRef
  with ExecutionContext
{

  // ExecutionContext
  final def execute(runnable: Runnable): Unit = runnable.run()
  final def reportFailure(cause: Throwable): Unit = throw cause

}
