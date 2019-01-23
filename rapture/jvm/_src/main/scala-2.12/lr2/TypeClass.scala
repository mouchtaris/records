package lr2

trait TypeClass[T] extends Any {
  final type R[V] = T ⇒ V
}
