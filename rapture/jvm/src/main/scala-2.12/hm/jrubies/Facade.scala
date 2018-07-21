package hm.jrubies

import java.nio.file.Path

final case class Facade(
  init: Boolean,
  rc_base_dir: Path,
  env: Env
)
{
  val rc = RcPathManager(init, rc_base_dir)
  object paths {
    val GEM_HOME = rc / "_rubygems"

    val bundler_base = rc / "_bundler_envs"
    val BUNDLE_APP_CONFIG = bundler_base / "_envs" / env.path / "_bundle"
  }

  val BUNDLER_VERSION = "1.16.2"

  object gem extends CommandLike {
    val args: Seq[String] = Vector("-S", "gem")

    val help: Command = this + "help"
    val help_install: Command = help + "install"
    val install: Command = gem + Vector(
      "install",
      "--install-dir", paths.GEM_HOME.path,
      "--no-document",
      "--no-wrappers",
      "--no-user-install",
      "--post-install-message",
      "--remote"
    )
  }
  val gem_install_bundler = gem_install + "bundler"
  val gem_install_pry = gem_install + "pry"
}
