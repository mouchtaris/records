package hm

object TypeInfo {
  final implicit class Name[T](val value: String) extends AnyVal
}

trait TypeInfo[T] extends Any {
  import TypeInfo._

  implicit def name: Name[T] = toString
}
