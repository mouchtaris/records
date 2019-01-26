package fn
package pfs.select

import pf.Def
import list.{ List, :: }

trait ReducerHighPriority extends Any with ReducerLowPriority {

  implicit def reducerOnDefinedDefinition[
    F,
    Zero <: List,
    In,
    R: Def[F]#at[In]#t,
  ]: ReducerOnDefined[F, Zero, In, R] =
    new ReducerOnDefined

}
