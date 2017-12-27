package gv
package building

trait DefaultConstructible[T] {
  def create(): T
}
