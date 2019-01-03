package lr

object lib {

  trait Lens[O, V] extends (O ⇒ V)

  trait Lensor[O] extends Any {

    type Lens[V] = lib.Lens[O, V]

  }

}
