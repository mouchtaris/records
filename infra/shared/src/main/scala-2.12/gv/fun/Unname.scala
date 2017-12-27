package gv
package fun

trait Unname

object Unname {

  implicit def definedUnname[b, n <: Named[b]]: Defined[Unname, n#T, b] =
    () ⇒ { case t ⇒ t }

  def apply[a, b](a: a)(implicit ev: Defined[Unname, a, b]): b =
    ev()(a)

}
