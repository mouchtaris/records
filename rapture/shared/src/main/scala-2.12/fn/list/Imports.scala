package fn.list
import fn.{ list ⇒ pkg }

trait Imports extends AnyRef {

  type List = pkg.List

  type ::[+A, +B <: List] = pkg.::[A, B]
  val :: : pkg.::.type = pkg.::

  type Nil = pkg.Nil
  val Nil: pkg.Nil.type = pkg.Nil

}
