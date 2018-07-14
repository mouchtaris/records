package detail.store

import _root_.store._
import akka.NotUsed

trait AkkaStreamContext extends Any with StreamContext {
  this: Any
    with FutureContext
  =>
  final type Source[T] = akka.stream.scaladsl.Source[T, NotUsed]
  final type Flow[In, Out] = akka.stream.scaladsl.Flow[In, Out, NotUsed]
}

