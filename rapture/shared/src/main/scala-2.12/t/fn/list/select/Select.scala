package t.fn
package list
package select

import reduce.Reduce

trait Select[F] extends Any with Def[Select[F]]

object Select {

  final implicit class Inst[F](val unit: Unit) extends AnyVal with Select[F]

  type Red[F] = Reduce[Nil, Reducer[F]]

  final implicit class Definition[F, In, R](val red: Def[Red[F]]#at[(Nil, In)]#t[R])
    extends AnyVal
    with Pf[Select[F], In]
  {
    override type Out = R
    override def apply(in: In): R = red((Nil, in))
  }

  implicit def selectDefinition[F, In, R: Def[Red[F]]#at[(Nil, In)]#t]: Definition[F, In, R] =
    new Definition(implicitly)

}
