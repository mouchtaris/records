package t.fn.pf

final class Applicator[F](val unit: Unit) extends AnyVal {
  def apply[A](a: A)(implicit f: F Pf A): f.Out =
    f(a)
}

