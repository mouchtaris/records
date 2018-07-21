package hm
package jrubies

final case class RcPathManager(
  init: Boolean,
  rcRoot: java.nio.file.Path
) {
  import java.nio.file.Paths.{ get ⇒ Path }

  def apply(p: String): RcPath  = RcPath(this, Path(p))
  def /(child: String): RcPath = apply(".") / child
}


