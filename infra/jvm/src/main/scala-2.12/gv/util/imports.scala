package gv
package util

object imports {

  trait Future {
    type Future[+T] = scala.concurrent.Future[T]
    val Future = scala.concurrent.Future
  }

}
