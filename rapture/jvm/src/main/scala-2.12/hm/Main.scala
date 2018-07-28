package hm

import java.io.OutputStreamWriter
import java.nio.charset.StandardCharsets
import java.nio.file.Paths

import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import hm.jrubies.{BundlerEnv, BundlerLike, GemLike, RcPathManager}
import org.jruby.embed.ScriptingContainer
import org.jruby.javasupport.JavaEmbedUtils

object Main {
  import List.{ ::, Nil }
  import Textable._

  object jruby {
    import hm.jrubies._

    val INIT = true
    val RC_DIR_SOURCE = Paths.get("rapture", "jvm", "src", "main", "resources")

  //  val irb = Command("-e", s"$gem_env\nrequire 'irb'\nIRB.start")

   // def gem_launcher(
   //   envs: Seq[String],
   //   stmts: Seq[String],
   //   gem: String,
   //   comm: String,
   //   args: Seq[String]
   // ): Command = {
   //   val _args =
   //     "-e" ::
   //     s"""
   //       | $gem_env
   //       | ${envs mkString "\n"}
   //       | require 'rubygems'
   //       | Gem.paths = GEM_PATHS
   //       |
   //       | ${stmts mkString "\n"}
   //       |
   //       | require 'pp'
   //       | pp RUBY_VERSION
   //       | pp ARGV
   //       | # pp Dir["#{GEM_HOME}/**"]
   //       |
   //       | gem '$gem', ANY_VERSION
   //       | load Gem.bin_path('$gem', '$comm', ANY_VERSION)
   //     """.stripMargin.stripMargin(' ') ::
   //     args.toList
   //   Command(_args: _*)
   // }
   // def gem_exec(gem: String, comm: String, args: String*): Command =
   //   gem_launcher(Seq.empty, Seq.empty, gem, comm, args)

   // val bundle = gem_exec("bundler", "bundle")
   // val bundle_help = bundle + "help"
   // val bundle_help_install = bundle_help + "install"
   // val bundle_help_package = bundle_help + "package"
   // val bundle_install = bundle + "install" + "--path" + BUNDLE_PATH.toString + "--no-cache"
   // val bundle_install_local = bundle_install + "--local"
   // val bundle_package = bundle + Seq("package", "--all")
   // def bundle_exec(gem: String, comm: String, args: String*)(implicit env: BundlerEnv): Command =
   //   gem_launcher(
   //     bundle_env,
   //     Seq(
   //       "gem 'bundler', BUNDLER_VERSION",
   //       "require 'bundler/setup'"
   //     ),
   //     gem,
   //     comm,
   //     args
   //   )

   // //val pry = bundle_exec + Seq("-rbundler/setup", "-rpry", "-epry")
   // def pry(implicit env: BundlerEnv) = bundle_exec("pry", "pry")
   // def rails(implicit env: BundlerEnv) = bundle_exec("railties", "rails")
   // def rails_console(implicit env: BundlerEnv) = rails + "console"
    // val pry = bundle_exec + "pry"
    // val rubocop = bundle_exec + BUNDLE_PATH.resolve("jruby/2.3.0/bin/rubocop").toString
  }

  val bun = new BundlerLike {
    val gem: GemLike = new GemLike {
      val rc: RcPathManager = RcPathManager(jruby.INIT, jruby.RC_DIR_SOURCE)
    }
    val env: BundlerEnv = BundlerEnv.musae.api
  }

  def main(args: Array[String]): Unit = {
    println("Geia soy theofile")
    println(1 :: 2 :: 3 :: "This is the shit" :: Nil)
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
    //import jrubies.BundlerEnv.musae.api
    //println {
    //  jruby.rails_console.args
    //}

    //hm.Incubate.closed_type_classes.test()
    //return ()
    2 match {
      case 1 ⇒
        bun.bundle_help_install
      case 2 ⇒
        bun.bundle_install
      case 3 ⇒
        bun.gem.install_bundler
      case _ ⇒
        bun.pry
    }()
  }
}
