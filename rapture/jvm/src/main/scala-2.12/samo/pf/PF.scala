package samo.pf

trait PF[F, A, B] extends Any {

  def apply(a: A): B

}

object PF {

  final class BindF[F](val unit: Unit) extends AnyVal {
    def apply[A, R](a: A)(implicit pf: PF[F, A, R]): R = pf(a)
  }

  def apply[F] = new BindF[F](())

  def apply[F](f: F): BindF[F] = apply[F]

}


