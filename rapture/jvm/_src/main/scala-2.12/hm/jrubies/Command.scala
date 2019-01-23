package hm
package jrubies

final class Command(val args: Seq[String]) extends AnyVal with CommandLike

object Command {
  def apply(args: String*): Command = new Command(args)
}

