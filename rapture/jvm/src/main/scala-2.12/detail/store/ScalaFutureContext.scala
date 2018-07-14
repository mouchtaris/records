package detail
package store

import _root_.store._

trait ScalaFutureContext extends Any
  with FutureContext
{
  final type Future[T] = scala.concurrent.Future[T]
}

