package gv
package tag

trait Tagverse extends Any {
  sealed trait tag
  final type Tagged[T] = T with tag

  final def apply[T](t: T): Tagged[T] = t.asInstanceOf[Tagged[T]]
  final def unapply[T](t: Tagged[T]): T = t
}


