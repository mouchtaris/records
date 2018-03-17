package infra
package apr
package list

trait ListOps[s <: List] extends Any {

  final type Self = s

  def self: Self

  final def ::[h](head: h): h :: Self = list.::(head, self)

}
