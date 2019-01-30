package hm
package jrubies

import java.nio.file.Paths.{ get ⇒ Path }

final case class RcPathManager(
  init: Boolean,
  rcRoot: java.nio.file.Path
) {
  def apply(p: String): RcPath  = RcPath(this, Path(p))
  def /(child: String): RcPath = apply(".") / child
}


