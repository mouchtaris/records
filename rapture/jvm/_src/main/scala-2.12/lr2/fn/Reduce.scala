package lr2
package t.fns

trait Reduce[S] extends Any {

  final type Next =
    S ⇒ Option[S]

  def zero: S

  def next: Next

}

