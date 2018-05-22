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

    final class Command(val args: String*) extends AnyVal {
      def +(moreArgs: Seq[String])= new Command(args ++ moreArgs: _*)
      def +(moreArg: String) = new Command(args :+ moreArg: _*)
      def apply(): Unit = {
        println(s"Running: ${args.mkString(" ")}")
        org.jruby.Main.main(args.toArray)
      }
    }

    trait Path[S <: Path[_]] extends Any {
      def self: java.nio.file.Path
      protected[this] def newFrom(path: java.nio.file.Path): S

      final def toUnixPath: String = {
        import scala.collection.JavaConverters._
        self.iterator().asScala.mkString("/")
      }

      final def resolve(other: String): S =
        newFrom(self.resolve(other))

      override def toString: String = {
        throw new Exception("Do not use")
      }
    }

    final implicit class PathForInit(val self: java.nio.file.Path) extends AnyVal with Path[PathForInit] {
      def newFrom(path: java.nio.file.Path): PathForInit = path
      override def toString: String = toUnixPath
    }

    final implicit class PathForRun(val self: java.nio.file.Path) extends AnyVal with Path[PathForRun] {
      def newFrom(path: java.nio.file.Path): PathForRun = path
      override def toString: String = s"uri:classloader:/$toUnixPath"
    }

    final type SomePath =
      AnyVal
        with Path[
          _
            >: PathForInit with PathForRun
            <: AnyVal with Path[
              _
                >: PathForInit with PathForRun
              ]
        ]

    val INIT = false
    val RC_DIR_SOURCE = Paths.get("rapture", "jvm", "src", "main", "resources")
    val RC_DIR: SomePath = if (INIT) new PathForInit(RC_DIR_SOURCE) else new PathForRun(RC_DIR_SOURCE)
    val GEM_HOME = RC_DIR.resolve("_rubyjams")
    val BUNDLE_PATH = RC_DIR.resolve("_bundlerjams")
    val BUNDLER_VERSION = "1.16.2"

    val gem = new Command("-S", "gem")
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

    val irb = new Command("-S", "irb")

    def bundle_exec(gem: String, comm: String, args: String*): Command = {
      val _args =
        "-rrubygems" ::
        "-e" ::
        s"""
          | # frozen_string_literal: true
          |
          | BUNDLER_VERSION = '$BUNDLER_VERSION'
          | GEM_HOME = '$GEM_HOME'
          | BUNDLE_PATH = '$BUNDLE_PATH'
          | ANY_VERSION = '>= 0.a'
          |
          | Gem.paths = { 'GEM_HOME' => GEM_HOME }
          | ENV['BUNDLE_PATH'] = BUNDLE_PATH
          | gem 'bundler', BUNDLER_VERSION
          | require 'bundler/setup'
          |
          | gem '$gem', ANY_VERSION
          | load Gem.bin_path('$gem', '$comm', ANY_VERSION)
        """.stripMargin ::
        args.toList
      new Command(_args: _*)
    }

    val bundle = bundle_exec("bunder", "bundle")
    val bundle_help = bundle + "help"
    val bundle_help_install = bundle_help + "install"
    val bundle_help_package = bundle_help + "package"
    val bundle_install = bundle + Seq(
      "install",
      "--clean",
      "--path", BUNDLE_PATH.toString
    )
    val bundle_install_local = bundle_install + "--local"
    val bundle_package = bundle + Seq("package", "--all")

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
    jruby.pry()
  }
}
