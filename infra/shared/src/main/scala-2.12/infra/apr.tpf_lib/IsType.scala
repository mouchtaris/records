package infra
package apr
package tpf_lib

import tpf._

sealed trait IsType[T] extends TPF

object IsType {

  final type Defined[T, at] = (IsType[T] Apply at) { type Out = T }

  implicit def `(at <:< T) ⇒ IsType`[T, at](implicit ev: at <:< T): Defined[T, at] =
    Apply(t ⇒ t)

}
