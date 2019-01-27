package t.fn.pfs.select

trait ReducerLowPriority extends Any {

  implicit def reducerOnUndefinedDefinition[F, Zero, In]: ReducerOnUndefined[F, Zero, In] =
    new ReducerOnUndefined(())

}
