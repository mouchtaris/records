package deckard

import scala.concurrent.ExecutionContext.Implicits.global
import akka.{Done, NotUsed}
import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import akka.stream.scaladsl.{Keep, Sink, Source ⇒ AkkaSource}

import scala.util.{Failure, Success, Try}


object Main {

  import Find._

  trait ActorSystems {
    implicit val sys = ActorSystem("bob")
    implicit val mat = ActorMaterializer()
  }

  def main(args: Array[String]): Unit = {
    Find.RootsMagnet.default.roots
      //.filter(_.path.last equalsIgnoreCase "C")
      // D:\_vbshare\mnms\cim_ste_toc
      .map { c ⇒ c.value.toPath.resolve("_vbshare/mnms/cim_ste_toc") }
      .map { Find.Pathname(_) }
      .map { p ⇒ println(s"[Pathname]{ pathname: $p }"); p }
      .collect { case Success(v) ⇒ v }
      .flatMap { p ⇒ val chi = p.children.toVector; println(
        s"""[Pathname: $p]{
        |  children_size: ${chi.size}
        |  children: $chi
        |  childran: ${p.closure}
        |  listf: ${p.value.listFiles.toVector; Vector.empty}
        |  das experimant: ${
          Option(p.value.listFiles)
            .map(_.toVector)
            .getOrElse(Stream.empty)
            .map(Option(_))
            .collect {
              case Some(file) if file.isFile => File(file)
              case Some(directory) if directory.isDirectory ⇒ Directory(directory)
              case Some(els) ⇒ File(new JFile(s"els://$els"))
              case els ⇒ File(new JFile(s"elsa://$els"))
            }
            ()
          }
        |};""".stripMargin);
          chi }
      .foreach(println)
  }
}
