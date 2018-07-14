package deckard

import java.io.File
import java.nio.file.{FileSystem, FileSystems, Path, Paths}

import scala.collection.JavaConverters._
import scala.util.{Failure, Success, Try}
import scala.collection.JavaConverters._

object Find {

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


  def apply(roots: RootsMagnet): Find =
    new Find(roots.roots)

  def apply(): Find =
    apply(implicitly)

}


class Find(
  roots: Iterable[File]
) {

  final object File {
    def unapply(file: File): Boolean =
      file.isFile
  }

  final object Directory {
    def unapply(file: File): Boolean =
      file.isDirectory
  }

  def allFilesInDirectory(dir: File): Stream[File] =
    Option(dir.listFiles)
      .map(_.toStream)
      .getOrElse(Stream.empty)
      .map(Option(_))
      .collect {
        case Some(file) => file
      }
      .flatMap(allFiles)

  def allFiles(file: File): Stream[File] = {
    file match {
      case File() =>
        Stream(file)
      case Directory() =>
        allFilesInDirectory(file)
    }
  }

  def allFiles: Stream[File] =
    roots.toStream.flatMap(allFiles)

}
