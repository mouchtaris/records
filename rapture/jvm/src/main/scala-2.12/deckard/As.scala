package deckard

object As {
  final type Evidence[T, U] = T => U

  final implicit class Decoration[T](val self: T) extends AnyVal {
    def as[U](implicit ev: Evidence[T, U]): U = ev(self)
  }
}



