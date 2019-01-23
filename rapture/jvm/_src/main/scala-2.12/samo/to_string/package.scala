package samo

package object to_string {

  final implicit def toStringDecoration[T](value: T): ToStringDecoration[T] = new ToStringDecoration[T](value)

}
