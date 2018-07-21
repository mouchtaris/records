package hm
package jrubies

final class Env(val value: Vector[String]) extends AnyVal {
  def from(base: Env): Env = new Env(base.value ++ value)
  def /(other: Env): Env = other from this
  def /(other: String): Env = new Env(value :+ other)
  def path: String = value mkString "/"
}

object Env {
  def apply(name: String*): Env = new Env(name.toVector)
  object musae {
    val base = Env("musae")
    implicit val api = base / "rails_api"
  }
}