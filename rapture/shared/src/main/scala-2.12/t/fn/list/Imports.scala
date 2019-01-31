package t.fn.list
import t.fn.{ list ⇒ pkg }

trait Imports extends AnyRef {

  type List = pkg.List

  type ::[+A, +B <: List] = pkg.::[A, B]
  val :: : pkg.::.type = pkg.::

  type Nil = pkg.Nil
  val Nil: pkg.Nil.type = pkg.Nil

  type Reduce[Zero, F] = reduce.Reduce[Zero, F]
  val Reduce: reduce.Reduce.type = reduce.Reduce

  type Select[F] = select.Select[F]
  val Select: select.Select.type = select.Select

}
