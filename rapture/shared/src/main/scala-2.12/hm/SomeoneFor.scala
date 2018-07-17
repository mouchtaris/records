package hm

object SomeoneFor {

  final implicit class SomeoneFor[Someone, For](val value: Someone) extends AnyVal

  sealed trait A[Someone] extends Any {
    final type For[F] = SomeoneFor[Someone, F]
  }

  object A {
    final class Builder[Someone](val value: Someone) extends AnyVal {
      def for_[For]: SomeoneFor[Someone, For] = value
    }

    def apply[Someone](value: Someone): Builder[Someone] = new Builder(value)
  }

}
