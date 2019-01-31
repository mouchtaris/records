package t.fn
package list
package select

trait SelectOps[S] extends Any {

  def self: S

  def select[F](f: F)(implicit ev: Pf[Select[F], S]): ev.Out =
    ev(self)

}
