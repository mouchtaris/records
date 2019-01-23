package store

trait StreamContext extends Any {
  type Source[T]
  type Flow[In, Out]
}


