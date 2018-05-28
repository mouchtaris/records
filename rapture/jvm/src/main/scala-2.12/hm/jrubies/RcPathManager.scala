package hm
package jrubies

final case class RcPathManager(
  init: Boolean,
  rcRoot: java.nio.file.Path
) {
  def apply(p: String) = RcPath(this, java.nio.file.Paths.get(p))
}


