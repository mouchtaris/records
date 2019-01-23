package samo.to_string

final class ToStringDecoration[T](val value: T) extends AnyVal {
  def string(implicit toString: ToString[T]): String = toString(value)
}
