package fn

trait TypeClass[S] extends Any {

  final type R[V] = S ⇒ V

}
