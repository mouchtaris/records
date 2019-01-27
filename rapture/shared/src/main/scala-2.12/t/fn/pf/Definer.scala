package t.fn.pf

final class Definer[F, In](val unit: Unit) extends AnyVal {
  def apply[Out](f: In ⇒ Out): Definition[F, In, Out] =
    new Definition(f)
}
