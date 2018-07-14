package deckard

import java.io.{File, FileFilter}
import java.nio.file.Path

import akka.{Done, NotUsed}
import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import akka.stream.scaladsl.{Keep, Sink, Source => AkkaSource}


object FileStore {
  def FileContext() = detail.store.FileContexts()
}

object Main {

  def main(args: Array[String]): Unit = {
    val find: Find = Find()
    // find.allFiles.foreach(println)

    val fs = FileStore.FileContext()
    val f = new fs.File {
      val tags: fs.Source[fs.Tag] = AkkaSource.fromIterator(() => List(
        new fs.Tag { val name = "bob"; val value = "lel" }
      ).toIterator)
      val data: fs.Source[Byte] = AkkaSource.fromIterator(() =>
        List[Byte](1, 2, 3, 4).toIterator
      )
    }

    implicit val sys = ActorSystem("bob")
    implicit val mat = ActorMaterializer()

    val foreachPrintln = Sink.foreach {
      tag: fs.Tag =>
        println(s" tag: ${tag.name} = ${tag.value} ")
    }
    val wait = f.tags.toMat(foreachPrintln)(Keep.right).run()

    import scala.concurrent.ExecutionContext.Implicits.global

    wait.onComplete { _ =>
      sys.terminate()
    }
  }
}
