package fn.pf

final class Definition[F, In, Out_](val self: In ⇒ Out_)
  extends AnyVal
    with Pf[F, In] {
  override type Out = Out_

  override def apply(in: In): Out = self(in)
}

object Definition {

  def apply[F, In, Out](self: In ⇒ Out): Definition[F, In, Out] =
    new Definition(self)
}
