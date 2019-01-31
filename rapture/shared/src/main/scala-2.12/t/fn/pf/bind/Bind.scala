package t.fn.pf.bind

import t.fn.list.List
import t.fn.pf.Def

sealed trait Bind[F, Args <: List] extends Def[Bind[F, Args]]

object Bind {

  final class BindDefinition[F, Args <: List, At <: List]

}
