package t.fn
package list
package select

trait ReducerHighPriority extends Any with ReducerLowPriority {

  implicit def reducerOnDefinedDefinition[
    F,
    Accum <: List,
    In,
    R: Def[F]#at[In]#t,
  ]: ReducerOnDefined[F, Accum, In, R] =
    new ReducerOnDefined(implicitly)

}
