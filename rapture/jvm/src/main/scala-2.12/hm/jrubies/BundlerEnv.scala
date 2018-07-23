package hm
package jrubies

final class BundlerEnv(val elements: Vector[String]) extends AnyVal with PathLike[BundlerEnv]

object BundlerEnv {

  def apply(names: String*): BundlerEnv =
    apply(names.toVector)
  
  implicit def apply(names: Vector[String]): BundlerEnv =
    new BundlerEnv(names)

  object musae {
    val base = BundlerEnv("musae")
    implicit val api = base / "rails_api"
  }

}