package samo.list

final class HeadDecoration[L](val value: L) extends AnyVal {
  def head[H](implicit head: L Head H): H =
    head.unapply(value).get
}
