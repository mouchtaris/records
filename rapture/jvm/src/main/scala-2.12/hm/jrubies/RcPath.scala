package hm
package jrubies

final case class RcPath(
  man: RcPathManager,
  self: java.nio.file.Path
) {
  import scala.collection.JavaConverters._

  protected[this] def resolvedSelf: java.nio.file.Path =
    if (man.init)
      man.rcRoot resolve self
    else
      self

  def toUnixPath: String =
    resolvedSelf.iterator().asScala.mkString("/")

  def resolve(other: String): RcPath =
    RcPath(man, self.resolve(other))
  def /(other: String) = resolve(other)

  def path: String =
    if (man.init)
      toUnixPath
    else
      s"uri:classloader:/$toUnixPath"

  override def toString: String = path
}


