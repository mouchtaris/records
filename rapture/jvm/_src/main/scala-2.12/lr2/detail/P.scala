package lr2
package detail

import xtrm.list
import list._
import lr2.adt.Production

object P {
  import S._

  //noinspection TypeAnnotation
  val productions =
    (goal → (expr :: Nil)) ::
      (expr → (term :: S.`+` :: expr :: Nil)) ::
      (expr → (term :: Nil)) ::
      (term → (factor :: S.`*` :: term :: Nil)) ::
      (term → (factor :: Nil)) ::
      (factor → (id :: Nil)) ::
      Nil


  trait IterableTo[T, A] extends Any with TypeClass[T] {
    def iterator: R[Iterator[A]]
  }

  object IterableTo {
    sealed trait To[A] { final type t[T] = T IterableTo A }
  }

  implicit def view[S: adt.Symbol, E: IterableTo.To[Closed[adt.Symbol]]#t]: adt.Production[(S, E)] =
    new Production[(S, E)] {
      override val symbol: R[Closed[adt.Symbol]] = prod ⇒ Closed[adt.Symbol](prod._1)
      override val expansion: R[Seq[Closed[adt.Symbol]]] =
        prod ⇒
          implicitly[IterableTo[E, Closed[adt.Symbol]]].iterator(prod._2).toSeq
    }
}
