package infra
package coloured

trait Coloured[T] extends Any {

  final type Self = T

  def self: Self

}

