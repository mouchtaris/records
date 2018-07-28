package hm.jrubies

trait BundlerLike
  extends Any
{
  def gem: GemLike
  def env: BundlerEnv

  private[this] def bundler_base: RcPath = gem.rc / "_bundler_envs" / env.path

  final def BUNDLER_VERSION = "1.16.2"
  final def BUNDLE_PATH: RcPath = bundler_base / "gems"
  final def BUNDLE_APP_CONFIG: RcPath = bundler_base / "_bundle"
  final def BUNDLE_GEMFILE: RcPath = bundler_base / "Gemfile"
  final def BUNDLE_CONFIG: RcPath = bundler_base / ".bundle"

  final def bundler_gem_env: Env =
    Env(
      constants = Map(
        Identifier("BUNDLER_VERSION") → Expression.string(BUNDLER_VERSION),
        Identifier("BUNDLE_GEMFILE") → Expression.string(BUNDLE_GEMFILE.path),
        Identifier("BUNDLE_CONFIG") → Expression.string(BUNDLE_CONFIG.path),
      ),
      statements = Vector.empty
    )

  final def bundle: Command =
    gem(bundler_gem_env, "bundler", "bundle")

  final def bundle_help: Command = bundle + "help"
  final def bundle_help_install: Command = bundle_help + "install"
  final def bundle_help_package: Command = bundle_help + "pacakge"
  final def bundle_install: Command =
    bundle + "install" +
      "--path" + bundler_base.self.relativize(BUNDLE_PATH.self).toString +
      "--no-cache" +
      "--gemfile" + BUNDLE_GEMFILE.path
  final def bundle_install_local: Command = bundle_install + "--local"
  final def bundle_package: Command = bundle + "package" + "--all"

  final def pry: Command = bundle + "pry"
}
