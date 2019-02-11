package hm
package jr

import java.nio.ByteBuffer
import java.nio.channels.SeekableByteChannel
import java.nio.file.{Files, Paths, StandardOpenOption}

import akka.util.ByteString

object Ruby {

  def main(args: Array[String]): Unit = {
    import t.jr.Hatchy.{ Ruby ⇒ HRuby, _ }

    val env = Env((rc / "_envs", "mse"))
    val ruby = HRuby(
      gemEnv = env,
      lib = rc,
      bundlerEnv = Some(env),
    )
    val engine = org.jruby.Ruby.newInstance()
    val MUFA = !true
    def exec(comm: Command): Unit = {
      val code: String = ruby.toCode(comm)
      val lined = code
          .lines
          .zipWithIndex
          .map { case (line, i) ⇒ "%03d:  %s".format(i, line) }
          .mkString("\n")
      println(s"*** Executing:\n$lined")
      if (!MUFA)
        engine.runFromMain(code, "")
    }


//    exec(ruby.install_gem("bundler", Some("1.17.3")))
    exec(ruby.gem_bin_stub("bundler", "bundle", Vector("help")))
//    exec(ruby.irb)
    ;{
//      val path = Paths.get("/home/nikos/inteli.tar")
//      val path = Paths.get("C:\\Users\\rudolf\\IdeaProjects\\rapture\\rapture\\jvm\\src\\main\\resources\\ruby_layer.rb")
//      val ins: SeekableByteChannel = Files.newByteChannel(path, StandardOpenOption.READ)
//
//      val toStream: ByteBuffer ⇒ Stream[Byte] =
//        bb ⇒ if (bb.hasRemaining) bb.get() #:: toStream(bb) else Stream.empty
//      val checksum: ByteBuffer ⇒ Int = {
//        bb ⇒
//          val sum = 0
//          bb.mark()
//          toStream(bb).sum
//      }
//
//      val chunks: SeekableByteChannel ⇒ Stream[ByteBuffer] =
//        channel ⇒ {
//          def stream(bb: ByteBuffer): Stream[ByteBuffer] = {
//            val read = channel.read(bb)
//            if (read == -1)
//              Stream.empty
//            else if (read == 0 && bb.hasRemaining)
//              stream(bb)
//            else {
//              bb.flip()
//              bb #:: {
//                bb.clear()
//                stream(bb)
//              }
//            }
//          }
//          stream(ByteBuffer.allocate(1))
//        }
//
//      type BBF = Stream[ByteBuffer]
//      type Mad[T] = BBF ⇒ Stream[T]
//      type Mod = Mad[BBF]
//
//      val lengths: Mad[Int] = _ map { _.remaining() }
//      val sums: Mad[Int] = _ map checksum
//      val op = chunks andThen sums
//
//      println(op(ins).toVector)
    }
  }


}
