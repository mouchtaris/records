package hm
package jrubies

final class Command(val args: Seq[String]) extends AnyVal with CommandOps

object Command {
  def apply(args: String*): Command = new Command(args)
}

