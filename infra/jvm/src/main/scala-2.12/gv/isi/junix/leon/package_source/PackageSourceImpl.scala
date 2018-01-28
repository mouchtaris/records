package gv
package isi
package junix
package leon
package package_source

import
  java.io.{
    IOException,
  },
  java.nio.file.{
    FileSystems,
    Path,
    Files,
    OpenOption,
    StandardOpenOption,
    FileAlreadyExistsException,
    FileSystem,
    CopyOption,
    StandardCopyOption,
    AtomicMoveNotSupportedException,
  },
  java.nio.channels.{
    FileChannel,
  },
  scala.collection.JavaConverters._,
  scala.concurrent.{
    Future,
    ExecutionContext,
  },
  scala.util.{
    Try,
    Success,
    Failure,
  },
  scala.util.control.Exception.{
    Catch,
    catching,
  },
  akka.stream.{
    IOResult,
  },
  akka.stream.scaladsl.{
    Source,
    Sink,
    FileIO,
  },
  akka.util.{
    ByteString,
  },
  akka.event.{
    LoggingAdapter,
  },
  lang.{
    LocalException,
    ThrowableDecoration,
  }

object PackageSourceImpl {

  final class FileOpsExecutionContext(val self: ExecutionContext) extends AnyVal {
    implicit def executionContext: ExecutionContext = self
  }

}

trait PackageSourceImpl
  extends AnyRef
  with PackageSource
{
  implicit def conf: config.PackageSource.Ext
  protected[this] val log: LoggingAdapter
  protected[this] val ec: PackageSourceImpl.FileOpsExecutionContext

  import PackageSource.{ Key, Result, Results }

  object privates
    extends AnyRef
    with LocalException
  {
    val filesystem: FileSystem = FileSystems.getDefault
    val root: Path = filesystem.getPath(conf.storageRoot)
    val NotThisResult: Try[Nothing] = Failure(new Exception)
    val OpenOptions: java.util.Set[OpenOption] = {
      val set: Set[OpenOption] = Set(
        StandardOpenOption.CREATE_NEW,
        StandardOpenOption.WRITE,
      )
      set.asJava
    }
    val CopyOptions = Seq[CopyOption](
      StandardCopyOption.ATOMIC_MOVE
    )
    val openCatcher: Catch[FileChannel] = catching[FileChannel](classOf[FileAlreadyExistsException])
    val moveCatcher: Catch[Path] =
      catching[Path](
        classOf[UnsupportedOperationException],
        classOf[FileAlreadyExistsException],
        classOf[IOException],
      )

    final case class Victim(key: Key.T) {

      final class LockFile(val suff: String = "") {
        def fullSuff = if (suff.isEmpty) suff else s".$suff"
        val path: Path = root resolve s"$key$fullSuff"
        val exists: Boolean = Files exists path
        val result: Try[Key.T] = Success(key)
        val length = path.toFile.length

        val maybe: Try[Key.T] =
          if (exists)
            result
          else
            NotThisResult

        def lock(): Try[FileChannel] =
          openCatcher withTry FileChannel.open(path, OpenOptions)

        def toSource: Source[ByteString, Future[IOResult]] =
          FileIO fromPath path

        def toSink: Sink[ByteString, Future[IOResult]] =
          FileIO toPath path
      }

      val path = new LockFile()
      val lock = new LockFile("lock")
      val fail = new LockFile("fail")

      val pathResult: Try[Results.Found] = path.maybe map { key ⇒ Results.Found(key, path.length, openSource()) }
      val failResult: Try[Results.Failed] = fail.maybe map Results.Failed
      val laterResult: Try[Results.Later] = lock.maybe map Results.Later

      def openSource(): Source[ByteString, Future[IOResult]] =
        path.toSource

      def moveTo(dst: LockFile): Try[Path] = {
        log debug s"move (${lock.path}, ${dst.path})"
        moveCatcher
          .withTry(Files.move(lock.path, dst.path, CopyOptions: _*))
          .map { r ⇒
            log debug s"it happened: $r"
            r
          }
      }

      def moveCompletedDownload(): Try[Path] = {
        log debug "dl complete, move lock"
        moveTo(path)
      }

      def markFailed(): Try[Path] = {
        moveTo(fail)
      }

      def openSink(): Sink[ByteString, Future[IOResult]] =
        lock.toSink.mapMaterializedValue { futureResult ⇒
          import ec._
          futureResult flatMap {
            case result @ IOResult(_, Success(_)) ⇒
              Future fromTry moveCompletedDownload() map (_ ⇒ result)
            case IOResult(_, Failure(ex)) ⇒
              log debug s"it's failed man, move to failed: ${ex.stackTrace}"
              Future fromTry markFailed() flatMap { _ ⇒ Future failed ex }
          }
        }

      def tryLock(): Try[Result] =
        lock.lock()
          .map(_.close())
          .flatMap(_ ⇒ lock.result)
          .map { key ⇒ Results.Missing(key, openSink()) }
          .recoverWith { case _ ⇒ get() }

      def get(): Try[Result] =
        failResult orElse laterResult orElse pathResult orElse tryLock()
    }
  }


  def apply(key: Key.T): Result =
    privates.Victim(key).get() match {
      case Success(result) ⇒ result
      case Failure(ex) ⇒ throw ex
    }
}
