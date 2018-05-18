package hm

import java.io.OutputStreamWriter
import java.nio.charset.StandardCharsets

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
  }
}