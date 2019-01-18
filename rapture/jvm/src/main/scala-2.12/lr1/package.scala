import scala.collection.generic.{ImmutableMapFactory, ImmutableSetFactory, IndexedSeqFactory}

package object lr1 {
  import
    scala.collection.immutable.{
      ListSet,
      Vector ⇒ Vector_,
      ListMap
    }

  type Set[T] = ListSet[T]
  val Set: ImmutableSetFactory[Set] = ListSet

  type Seq[T] = Vector_[T]
  val Seq: IndexedSeqFactory[Vector] = Vector_

  type Map[K, +V] = ListMap[K, V]
  val Map: ImmutableMapFactory[Map] = ListMap
}
