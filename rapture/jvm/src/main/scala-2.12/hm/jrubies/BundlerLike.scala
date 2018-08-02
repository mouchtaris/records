package hm.jrubies

trait BundlerLike
  extends Any
{
  def gem: GemLike
  def env: BundlerEnv

  private[this] def bundler_base: RcPath = gem.rc / "_bundler_envs" / env.path

  final def BUNDLER_VERSION = "1.16.2"
  final def BUNDLE_PATH: RcPath = gem.rc / "_bundler_gems"
  final def BUNDLE_GEMFILE: RcPath = bundler_base / "Gemfile"

  final def bundler_gem_env: Env = {
    val conf = Seq(
      "BUNDLER_VERSION" → BUNDLER_VERSION,
      "BUNDLE_GEMFILE" → BUNDLE_GEMFILE.path,
      "BUNDLE_PATH" -> BUNDLE_PATH.path,
    )
    val constants = conf
      .map { case (name, value) => Identifier(name) -> Expression.string(value) }
    val statements = conf
      .map { case (name, value) => Expression(s"ENV['$name'] = $name") }
    Env(
      constants = constants.toMap,
      statements = statements :+ Expression("require 'bundler/setup'"),
    )
  }

  final def bundle: Command =
    gem(Env.empty, "bundler", "bundle")

  final def bundle_help: Command = bundle + "help"
  final def bundle_help_install: Command = bundle_help + "install"
  final def bundle_help_package: Command = bundle_help + "pacakge"
  final def bundle_init: Command = bundle + "init"
  final def bundle_install: Command =
    bundle + "install" +
      "--path" + bundler_base.self.relativize(BUNDLE_PATH.self).toString +
      "--no-cache" +
      "--gemfile" + BUNDLE_GEMFILE.path
  final def bundle_install_local: Command = bundle_install + "--local"
  final def bundle_package: Command = bundle + "package" + "--all"

  final def apply(
    gemName: String,
    bin: String
  ): Command =
    gem(bundler_gem_env, gemName, bin)

  final def pry: Command = this("pry", "pry")
}
