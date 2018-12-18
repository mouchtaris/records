package deckard

import java.io.{File, FileFilter}
import java.nio.file.Path

import scala.concurrent.ExecutionContext.Implicits.global
import akka.{Done, NotUsed}
import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import akka.stream.scaladsl.{Keep, Sink, Source ⇒ AkkaSource}

import scala.util.{Failure, Success}


object Main {

  def main(args: Array[String]): Unit = {
    implicit val sys = ActorSystem("bob")
    implicit val mat = ActorMaterializer()

  }
}
