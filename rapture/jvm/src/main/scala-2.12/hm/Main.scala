package hm

import java.io.OutputStreamWriter
import java.nio.charset.StandardCharsets
import java.nio.file.{Paths}

import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import org.jruby.embed.ScriptingContainer
import org.jruby.javasupport.JavaEmbedUtils

object Main {
  import List.{ ::, Nil }
  import Textable._

  object jruby {
    import hm.jrubies._

    val INIT = !true

    val RC_DIR_SOURCE = Paths.get("rapture", "jvm", "src", "main", "resources")
    val rcMan = RcPathManager(INIT, RC_DIR_SOURCE)
    val RC_DIR = rcMan(".")
    val GEM_HOME = RC_DIR / "_rubyjams"
    val BUNDLE_PATH = RC_DIR / "_bundlerjams"
    val BUNDLE_APP_CONFIG = RC_DIR / "_bundle"
    val BUNDLER_VERSION = "1.16.2"

    val gem = Command("-S", "gem")
    val gem_help = gem + "help"
    val gem_help_install = gem_help + "install"
    val gem_install = gem + Seq(
      "install",
      "--install-dir", GEM_HOME.toString,
      "--no-document",
      "--no-wrappers",
      "--no-user-install",
      "--post-install-message",
      "--remote"
    )
    val gem_install_bundler = gem_install + "bundler"
    val gem_install_pry = gem_install + "pry"

    val gem_env: String =
      s"""
         |# frozen_string_literal: true
         |
         |BUNDLER_VERSION           = '$BUNDLER_VERSION'
         |GEM_HOME                  = '$GEM_HOME'
         |BUNDLE_APP_CONFIG         = '$BUNDLE_APP_CONFIG'
         |
         |ANY_VERSION               = '>= 0.a'
         |GEM_PATHS                 = { 'GEM_HOME' => GEM_HOME }.freeze
         |
         |ENV['BUNDLE_APP_CONFIG']  = BUNDLE_APP_CONFIG
       """.stripMargin

    val irb = Command("-e", s"$gem_env\nrequire 'irb'\nIRB.start")

    def gem_launcher(
      stmts: Seq[String],
      gem: String,
      comm: String,
      args: Seq[String]
    ): Command = {
      val _args =
        "-e" ::
        s"""
          | $gem_env
          | require 'rubygems'
          | Gem.paths = GEM_PATHS
          |
          | ${stmts mkString "\n"}
          |
          | require 'pp'
          | pp RUBY_VERSION
          | pp ARGV
          | pp Dir["#{GEM_HOME}/**"]
          |
          | gem '$gem', ANY_VERSION
          | load Gem.bin_path('$gem', '$comm', ANY_VERSION)
        """.stripMargin.stripMargin(' ') ::
        args.toList
      Command(_args: _*)
    }
    def gem_exec(gem: String, comm: String, args: String*): Command =
      gem_launcher(Seq.empty, gem, comm, args)

    val bundle = gem_exec("bundler", "bundle")
    val bundle_help = bundle + "help"
    val bundle_help_install = bundle_help + "install"
    val bundle_help_package = bundle_help + "package"
    val bundle_install = bundle + "install"
    val bundle_install_local = bundle_install + "--local"
    val bundle_package = bundle + Seq("package", "--all")
    def bundle_exec(gem: String, comm: String, args: String*): Command =
      gem_launcher(
        Seq(
          "gem 'bundler', BUNDLER_VERSION",
          "require 'bundler/setup'"
        ),
        gem,
        comm,
        args
      )

    //val pry = bundle_exec + Seq("-rbundler/setup", "-rpry", "-epry")
    val pry = bundle_exec("pry", "pry")
    // val pry = bundle_exec + "pry"
    // val rubocop = bundle_exec + BUNDLE_PATH.resolve("jruby/2.3.0/bin/rubocop").toString
  }

  def main(args: Array[String]): Unit = {
//    println("Geia soy theofile")
//    println(1 :: 2 :: 3 :: "This is the shit" :: Nil)
//    val w = new OutputStreamWriter(System.out, StandardCharsets.UTF_8)
//    model.Email("ol").writeTo(w)
//    w.flush()
//    w.close()
//    val config = hm.config()
//    val g = new hm.Google
//    implicit val actorSystem = ActorSystem("LeBobs")
//    implicit val materializer = ActorMaterializer()
//    import scala.concurrent.ExecutionContext.Implicits.global
//    val s = new hm.HttpServer(config.server)(g)
//    s.binding.foreach(println)
//    import org.jruby.Main.{ main ⇒ jruby }
//    val bundle_help = Array(
//      "c:\\users\\rudolf\\.gem\\jruby\\2.3.0\\bin\\bundle",
//      "install",
//      "--help"
//    )
//    val gem_help = Array("-S", "gem", "--help")
//    val gem_help_install = Array("-S", "gem", "help", "install")
//    val gem_install_bundler = Array("-S", "gem", "install", "--install-dir", "./lolgems", "bundler")
//    val bundle_help_install = Array("-S", "./lolgems/bin/bundle", "help", "install")
//    jruby(bundle_help_install)
    // jruby.gem_install_pry()
    jruby.pry()
  }
}
