package infra
package coloured

trait Conversions extends Any {

  implicit def selfFromColouredSelf[T](implicit c: Coloured[T]): T =
    c.self

}
