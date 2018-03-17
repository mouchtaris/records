package infra
package nineties
package init

import
  scala.concurrent.{
    Future,
  },
  AntePaliRecordsLib._,
  apr._

trait Init[T]
  extends AnyRef
  with types.Type
{

  final type Base = Future[T]
  final type Out = t

  final def t: this.t = this(this())

  def apply(): Base
}

object Init {

  def apply[t](implicit init: Init[t]): init.type =
    init

  implicit def apply[t: Init]()(implicit init: Init[t]): init.t =
    init.t

}


