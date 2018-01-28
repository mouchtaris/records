package gv
package util

object awaitable {

  final implicit class DurationDye(val self: scala.concurrent.duration.Duration) extends AnyVal

  final implicit class AwaitableOps[T](val self: scala.concurrent.Awaitable[T]) extends AnyVal {
    def awaitResult(implicit d: DurationDye): T = scala.concurrent.Await.result(self, d.self)
  }

}
