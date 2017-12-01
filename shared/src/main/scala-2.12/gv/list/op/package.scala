package gv
package list

import
  fun._

package object op
  extends AnyRef
{

  final implicit class Decor[list <: List](val self: list)
    extends AnyVal
    with find.FindDecoration[list]
  {
    def get[t <: Type](t: t)(implicit get: Get[t, list], ev: t#T <:< t.T): t.T = ev(get(self))
  }

  type Contains[T, list <: List] = find.Evidence[TypeEquiv[T], list, T]
  object contained {
    sealed trait in[list <: List] {
      type λ[t] = Contains[t, list]
    }
  }

  type Get[T <: Type, list <: List] = find.Evidence[IsType[T], list, T#T]

}
