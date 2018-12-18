package deckard

import java.io.{ File ⇒ JFile }
import java.nio.file.{FileSystem, FileSystems, Path, Paths}

import scala.collection.JavaConverters._
import scala.util.{Failure, Success, Try}
import scala.collection.JavaConverters._

import akka.stream.scaladsl.{ Source, Flow, Sink, FileIO }

object Find {

  final implicit class File(val value: JFile) extends AnyVal
  final implicit class Directory(val value: JFile) extends AnyVal {
    def files: Stream[JFile] =
      Option(value.listFiles)
        .map(_.toStream)
        .getOrElse(Stream.empty)
        .map(Option(_))
        .collect {
          case Some(file) => file
        }
        .flatMap(allFiles)

  }

  def allFilesInDirectory(dir: JFile): Stream[JFile] =
    Option(dir.listFiles)
      .map(_.toStream)
      .getOrElse(Stream.empty)
      .map(Option(_))
      .collect {
        case Some(file) => file
      }
      .flatMap(allFiles)

  final object File {
    def unapply(file: JFile): Option[File] =
      if (file.isFile)
        Some(File(file))
    else
        None
  }

  final object Directory {
    def unapply(file: JFile): Option[Directory] =
      if (file.isDirectory)
        Some(Directory(file))
    else
        None
  }

  def allFiles(file: JFile): Stream[JFile] = {
    file match {
      case File(_) =>
        Stream(file)
      case Directory(dir) =>
        dir.files
    }
  }

  implicit val pathToFile: As.Evidence[Path, File] =
    _.toFile

  final class RootsMagnet(val roots: Iterable[File]) extends AnyVal

  object RootsMagnet {
    import As.Decoration

    implicit def fromToFileIterable[T](roots: Iterable[T])(implicit ev: As.Evidence[T, File]): RootsMagnet =
      new RootsMagnet(roots map (_.as[File]))

    implicit def default: RootsMagnet =
      FileSystems.getDefault.getRootDirectories.asScala
  }

}

