package xtrm

object list_get {
  import list._
  import get._

  implicit val GetNil: Get[Nil, Nil] =
    identity

  implicit def getFromHead[H]: Get[H :: List, H] =
    _.head

  implicit def getFromTail[G, T <: List](implicit tev: Get[T, G]): Get[_ :: T, G] =
    list ⇒ tev(list.tail)
}
