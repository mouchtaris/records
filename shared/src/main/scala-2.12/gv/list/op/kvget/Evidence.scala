package gv
package list
package op.kvget

trait Evidence[a <: List, b <: List, k, v] extends Any {
  def apply(b: b): v
}
