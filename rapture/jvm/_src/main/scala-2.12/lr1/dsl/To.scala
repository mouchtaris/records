package lr1
package dsl

trait To[-A, +B] extends Any {

  def apply(obj: A): B

}
