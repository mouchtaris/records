package t
package lr

import fn.list.{ Nil, :: }

object Sample {

  sealed trait goal
  sealed trait expr
  sealed trait term
  sealed trait factor
  sealed trait id
  sealed trait plus
  sealed trait star

  type g1 =
    (goal :: expr :: Nil) ::
      (expr :: term :: plus :: expr :: Nil) ::
      (expr :: term :: Nil) ::
      (term :: factor :: star :: term :: Nil) ::
      (term :: factor :: Nil) ::
      (factor :: id :: Nil) ::
      Nil
}
