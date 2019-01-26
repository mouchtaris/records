package lr2
package fns

trait Reduce[S] extends Any {

  final type Next =
    S ⇒ Option[S]

  def zero: S

  def next: Next

}

