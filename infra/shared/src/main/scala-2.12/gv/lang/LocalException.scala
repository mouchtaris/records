package gv
package lang

trait LocalException {

  class Exception(
    msg: String = null,
    cause: Throwable = null
  ) extends scala.Exception(msg, cause)

}
