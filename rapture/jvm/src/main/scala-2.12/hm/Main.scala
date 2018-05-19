package hm

import java.io.OutputStreamWriter
import java.nio.charset.StandardCharsets

import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import org.jruby.embed.ScriptingContainer
import org.jruby.javasupport.JavaEmbedUtils

object Main {
  import List.{ ::, Nil }
  import Textable._

  def main(args: Array[String]): Unit = {
    println("Geia soy theofile")
    println(1 :: 2 :: 3 :: "This is the shit" :: Nil)
    val w = new OutputStreamWriter(System.out, StandardCharsets.UTF_8)
    model.Email("ol").writeTo(w)
    w.flush()
    w.close()
    val g = new hm.Google
    implicit val actorSystem = ActorSystem("LeBobs")
    implicit val materializer = ActorMaterializer()
    import scala.concurrent.ExecutionContext.Implicits.global
    val s = new hm.HttpServer(g)
    s.binding.foreach(println)
  }
}