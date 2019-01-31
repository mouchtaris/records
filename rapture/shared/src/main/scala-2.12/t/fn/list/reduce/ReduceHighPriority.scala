package t.fn
package list
package reduce

trait ReduceHighPriority extends Any with ReduceLowPriority {

  implicit def reduceList[
    Zero,
    F,
    H,
    T <: List,
    RT: Def[Reduce[Zero, F]]#at[(Zero, T)]#t,
    R: Def[F]#at[(RT, H)]#t,
  ]: ReduceListDefinition[Zero, F, H, T, RT, R] =
    new ReduceListDefinition

  implicit def reduceNil[Zero, F]: ReduceNilDefinition[Zero, F] =
    new ReduceNilDefinition(())

}


