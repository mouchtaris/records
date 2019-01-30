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
  }

  val bun = new BundlerLike {
    val gem: GemLike = new GemLike {
      val rc: RcPathManager = RcPathManager(jruby.INIT, jruby.RC_DIR_SOURCE)
    }
    val env: BundlerEnv = BundlerEnv
      .musae.api
      //.malthael
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
    //hm.Incubate.closed_type_classes.test()
    (
      7 match {
        case 1 ⇒
          bun.bundle_help_install
        case 2 ⇒
          bun.bundle_install
        case 3 ⇒
          bun.gem.install_bundler("1.17.3")
        case 4 ⇒
          bun.pry
        case 5 ⇒
          bun.bundle_init
        case 6 ⇒
          bun.haml_index
        case 7 ⇒
          bun.bundle1_install
      }
    )()
  }
}
