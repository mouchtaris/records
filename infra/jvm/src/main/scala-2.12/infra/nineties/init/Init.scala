package infra
package nineties
package init

import
  scala.concurrent.{
    Future,
  }

trait Init[t] extends (() ⇒ Future[t]) {

  final type Out = Future[t]

}

object Init {

  def apply[t: Init]: Init[t] =
    implicitly

  implicit def apply[t]()(implicit init: Init[t]): init.Out  =
    init()

}


