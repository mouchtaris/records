package hm
package jrubies

trait GemLike
  extends Any
  with CommandLike
{
  def rc: RcPathManager

  //
  // CommandLike
  //
  final def args: Vector[String] = Vector("-S", "gem")

  //
  // GemLike
  //
  final def GEM_HOME: RcPath = rc / "_rubygems"
  //
  final def help: Command = this + "help"
  final def help_install: Command = help + "install"
  //
  final def install(name: String, version: String = ""): Command = this + Vector(
    "install",
    "--install-dir", GEM_HOME.path,
    "--no-document",
    "--no-wrappers",
    "--no-user-install",
    "--post-install-message",
    "--remote",
    if (version.nonEmpty) s"$name:$version" else name
  )
  final def install_bundler(version: String = ""): Command = install("bundler", version)
  final def install_rainbow(version: String = ""): Command = install("rainbow", version)

  final def prelude_rb: String =
    s"""
       |# frozen_string_literal: true
       |
       |GEM_HOME                  = '$GEM_HOME'
       |ANY_VERSION               = '>= 0.a'
       |GEM_PATHS                 = { 'GEM_HOME' => GEM_HOME }.freeze
       |
       """.stripMargin

  final def apply(
    env: Env,
    gemName: String,
    bin: String,
    version: Option[String] = None,
  ): Command =
    Command(
      Vector(
        "-e",
        s"""
           |$prelude_rb
           |${env.constants_rb}
           |
           |require 'rubygems'
           |Gem.paths = GEM_PATHS
           |
           |${env.statements_rb}
           |
           |require 'pp'
           |pp RUBY_VERSION
           |pp ARGV
           |# pp Dir["#{GEM_HOME}/**"]
           |
           |GEM_NAME = '$gemName'
           |GEM_BIN = '$bin'
           |GEM_VERSION = ${version.map(v ⇒ s"'$v'") getOrElse "ANY_VERSION"}
           |
           |gem GEM_NAME, GEM_VERSION
           |load Gem.bin_path(GEM_NAME, GEM_BIN, GEM_VERSION)
         """.stripMargin
      ): _*
    )
}
