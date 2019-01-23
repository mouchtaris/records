package lr2
package adt

trait Symbol[T] extends AnyRef with TypeClass[T] {
  val name: R[String]
}
