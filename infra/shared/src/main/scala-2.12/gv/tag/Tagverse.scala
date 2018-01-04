package gv
package tag

trait Tagverse extends Any {
  sealed trait tag extends Any
  final type Tagged[t] = t with tag { type T = t }

  final def apply[T](t: T): Tagged[T] = t.asInstanceOf[Tagged[T]]
  final def unapply[T](t: Tagged[T]): Option[T] = Some(t)
}


