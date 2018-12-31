package deckard

import java.nio.file.{FileSystems}

import scala.collection.JavaConverters._
import scala.util.{Failure, Success, Try}

import panda.Magnet

object Find {

  type JFile = java.io.File
  type JPath = java.nio.file.Path

  implicit val jpathToJfileMagnetization: JPath Magnet JFile =
    _.toFile

  final class Path(val self: Traversable[String]) extends AnyVal {
    def clean = new Path(Seq.empty)
  }

  trait Pathname extends Any {
    def value: JFile

    def children: Stream[Pathname] =
      Option(value.listFiles)
        .map(_.toStream)
        .getOrElse(Stream.empty)
        .map(Option(_))
        .collect {
          case Some(file) if file.isFile => File(file)
          case Some(directory) if directory.isDirectory ⇒ Directory(directory)
        }

    final def closure: Stream[Pathname] =
      this #:: (children flatMap (_.closure))

    def isFile: Boolean = false
    def isDirectory: Boolean = false

    final def path: Stream[String] =
      value.toURI.getPath.split("/").toStream
  }

  final implicit class File(val value: JFile) extends AnyVal with Pathname {
    override def isFile: Boolean = true
    override def toString: String = s"File($value)"
  }

  final implicit class Directory(val value: JFile) extends AnyVal with Pathname {
    override def isDirectory: Boolean = true
    override def toString: String = s"Directory($value)"
  }

  final object Pathname {

    def apply[T: Magnet.ized[Try[JFile]]#from](file: T): Try[Pathname] =
      Magnet[Try[JFile]](file) flatMap {
        case f if f.isFile ⇒
          Success(File(f))
        case d if d.isDirectory ⇒
          Success(Directory(d))
        case f ⇒
          Failure(new Exception(s"Unknown file type: $f"))
      }

  }

  final object File {
    def unapply[T: Magnet.ized[JFile]#from](file: T): Option[File] =
      Magnet[JFile](file) match {
        case f if f.isFile ⇒
          Some(File(f))
        case _ ⇒
          None
      }
  }

  final object Directory {
    def unapply[T: Magnet.ized[JFile]#from](file: T): Option[Directory] =
      Magnet[JFile](file) match {
        case f if f.isDirectory ⇒
          Some(Directory(f))
        case _ ⇒
          None
      }
  }

  implicit def pathname(path: JPath): Try[Pathname] = pathname(path.toFile)
  implicit def pathname(file: JFile): Try[Pathname] = Pathname(file)

  final class RootsMagnet(val roots: Stream[Pathname]) extends AnyVal

  object RootsMagnet {

    implicit def fromToFileIterable[T](roots: Iterable[T])(implicit ev: T ⇒ Try[Pathname]): RootsMagnet =
      new RootsMagnet(roots.toStream map ev collect { case Success(path) ⇒ path })

    implicit def default: RootsMagnet =
      FileSystems.getDefault.getRootDirectories.asScala
  }

}

