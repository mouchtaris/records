package samo.list

final class TailDecoration[L](val value: L) extends AnyVal {
  def tail[T](implicit tail: L Tail T): T =
    tail.unapply(value).get
}
