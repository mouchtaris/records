package infra
package apr
package types_tpf

import types._
import tpf._

sealed trait IsType[T <: Type] extends TPF

object IsType {

  type Defined[T <: Type, at, out] = Apply[IsType[T], at] { type Out = out }

  implicit def `T => IsType`[T <: Type](implicit T: T): Defined[T, T.t, T.t] =
    Apply(t ⇒ t)

}
