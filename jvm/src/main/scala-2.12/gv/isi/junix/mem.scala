package gv
package isi
package junix

import
  java.nio.{ CharBuffer, ByteBuffer, BufferOverflowException, BufferUnderflowException },
  java.nio.charset.{ StandardCharsets },
  java.nio.channels.{ Channels, WritableByteChannel, ReadableByteChannel },
  java.util.concurrent.{ Executors },
  java.nio.file.{ Files, Path, Paths },
  java.io.{ ByteArrayOutputStream, ByteArrayInputStream, OutputStreamWriter, FileOutputStream, File, BufferedOutputStream },
  scala.collection.{ mutable },
  scala.concurrent.{ ExecutionContext, Future, Promise, Await, Awaitable },
  scala.concurrent.duration._,
  scala.util.{ Try, Failure, Success },
  scala.annotation.{ tailrec },
  akka.actor.{ Actor, ActorSystem, Terminated, Props, ActorRef },
  akka.pattern.{ ask, pipe },
  akka.util.{ Timeout, ByteString },
  akka.{ Done, NotUsed },
  akka.stream.{ Materializer, ActorMaterializerSettings, ActorMaterializer, UniformFanOutShape, UniformFanInShape, FlowShape, IOResult },
  akka.stream.scaladsl.{ Flow, Sink, Source, Merge, GraphDSL, Partition, FileIO, Concat, RunnableGraph, Keep }

object mem {

  final implicit class TapDecoration[T](val self: T) extends AnyVal {
    def tap(f: T ⇒ Unit): T = {
      f(self)
      self
    }
  }

  final implicit class AwaitableDecoration[T](val self: Awaitable[T]) extends AnyVal {
    def await(implicit to: Timeout) = Await.ready(self, to.duration)
  }

  def cond[T, R](x: T)(pf: PartialFunction[T, R]): R = pf(x)
  def ignoring1[T](f: ⇒ Unit): T ⇒ Unit = _ ⇒ f

  final implicit class WeirdConversionsDeco[T](val self: T) extends AnyVal {
    def toSuccessUnit: Success[Unit] = Success(())
  }

  trait EffectCompanion[f[_]] { final type F[a] = f[a] }
  object FFuture extends EffectCompanion[Future]
  object FTry extends EffectCompanion[Try]
  object FSuccess extends EffectCompanion[Success]

  final implicit class ToDecoration[T](val self: T) extends AnyVal {
    def as[U](implicit ev: T ⇒ U): U = ev(self)
    def as[f[_], u[_], p](f: EffectCompanion[f])(implicit tev: T <:< u[p], ev: u[p] ⇒ f[p]): f[p] = ev(tev(self))
  }

  object Now extends ExecutionContext {
    override def execute(runnable: Runnable): Unit = runnable.run()
    override def reportFailure(cause: Throwable): Unit = throw cause
  }

  type Bytes = Array[Byte]

  trait ToData[T] {
    def apply(byteBuffer: ByteBuffer, obj: T): Try[Unit]
  }

  object ToData {
    final val StringEncoding = StandardCharsets.UTF_8
    final val StringEncoder = StringEncoding.newEncoder()
    final val StringDecoder = StringEncoding.newDecoder()

    implicit val longToData: ToData[Long] = _.putLong(_).toSuccessUnit
    implicit val bytesToData: ToData[Bytes] = { (bb, bytes) ⇒
      longToData(bb, bytes.length)
      bb.put(bytes).toSuccessUnit
    }
    implicit val stringToData: ToData[String] = (bb, str) ⇒ {
      StringEncoder.reset()
      val cb = CharBuffer.wrap(str)
      longToData(bb, str.length)
      StringEncoder.encode(cb, bb, true)
      if (cb.remaining() > 0)
        Failure(new BufferOverflowException)
      else
        Success(())
    }
  }

  trait FromData[T] {
    def apply(byteBuffer: ByteBuffer): T
  }

  object FromData {
    def apply[T: FromData](implicit bb: ByteBuffer): T = bb.to[T]
    implicit val longFromData: FromData[Long] = _.getLong
    implicit val bytesFromData: FromData[Bytes] = bb ⇒ {
      val length = longFromData(bb).toInt
      val bytes = new Array[Byte](length)
      bb.get(bytes)
      bytes
    }
    implicit val stringFromData: FromData[String] = bb ⇒ {
      val length = longFromData(bb).toInt
      ToData.StringDecoder.reset()
      val cb = CharBuffer.allocate(length)
      ToData.StringDecoder.decode(bb, cb, true)
      cb.flip()
      cb.toString
    }
  }

  final implicit class ToDataDecoration[T](val self: T) extends AnyVal {
    type ev = ToData[T]
    def toData(implicit bb: ByteBuffer, ev: ev): ByteBuffer = {
      bb.clear()
      ev(bb, self)
      bb.flip()
      bb
    }
  }

  final implicit class FromDataDecoration(val self: ByteBuffer) extends AnyVal {
    def to[T](implicit bb: ByteBuffer, ev: FromData[T]): T = ev(bb)
  }

  final case class ThreadContext(
    bb: ByteBuffer = ByteBuffer.allocate(1 << 20)
  )

  sealed trait FileType
  object DirectoryFile extends FileType { def unapply(file: File) = Some(file) filter (_.isDirectory) }
  object FileFile extends FileType { def unapply(file: File) = Some(file) filter (_.isFile) }
  object FileType {
    def apply(file: File): FileType = cond(file) {
      case DirectoryFile(_) ⇒ DirectoryFile
      case FileFile(_) ⇒ FileFile
    }
  }

  final case class FileWithType(file: File, tp: FileType)

  final implicit class FIdentitySideEffect[T](val self: T ⇒ Unit) extends AnyVal
  object FIdentitySideEffect {
    val noFX: Any ⇒ Unit = _ ⇒ ()
    implicit def defaultNoFX[T]: FIdentitySideEffect[T] = noFX
  }
  def fidentity[T](implicit sef: FIdentitySideEffect[T]): Flow[T, T, NotUsed] = Flow fromFunction { obj ⇒
    sef.self(obj)
    obj
  }

  val fileSeed: File = Paths.get("/mem").toFile
  val files: Source[File, NotUsed] = Source single fileSeed
  def findFilesFunction: File ⇒ Stream[File] = {
    case DirectoryFile(file) ⇒
      file.listFiles.flatMap(findFilesFunction).toStream
    case FileFile(file) ⇒
      Stream(file)
  }
  val findFiles: Flow[File, File, NotUsed] = Flow[File] flatMapConcat (findFilesFunction andThen Source.apply)
  val loadFile: Flow[File, (File, ByteString), NotUsed] = Flow[File]
    .flatMapConcat { file ⇒ FileIO.fromPath(file.toPath).map { (file, _) } }
  val serializeFile: Flow[(File, ByteString), ByteString, NotUsed] = Flow[(File, ByteString)]
    .flatMapConcat {
      case (file, bs) ⇒
        implicit val bb = ByteBuffer.allocate(1 << 20)
        Source fromIterator Vector(
          ByteString(file.getName.toData),
          ByteString(bs.length.toLong.toData),
          bs
        ).iterator _
    }

  object BBManager {
    final val MAX = 10
    case object Allocate
    case class Deallocate(bb: ByteBuffer)
    case object State
  }
  class BBManager extends Actor {
    object state {
      val free: mutable.Queue[ByteBuffer] = mutable.Queue.empty
      val allocated: mutable.Set[ByteBuffer] = mutable.Set.empty
      val waiters: mutable.Queue[ActorRef] = mutable.Queue.empty
    }
    import state._

    def newbb: ByteBuffer = ByteBuffer.allocate(1 << 20)
    def getbb: ByteBuffer = if (free.isEmpty) newbb else free.dequeue()
    def allocatebb: ByteBuffer = getbb tap allocated.+=
    val deallocate: ByteBuffer ⇒ Unit = _.tap(allocated.-=).tap(free.enqueue(_))

    import BBManager._
    def receive: Receive = {
      case State ⇒
        sender ! state
      case Allocate ⇒
        if (allocated.size >= MAX)
          waiters.enqueue(sender)
        else
          sender ! allocatebb
      case Deallocate(bb) ⇒
        bb.clear()
        if (waiters.nonEmpty)
          waiters.dequeue() ! bb
        else
          deallocate(bb)
    }
  }
  final implicit class BBManagerActorRef(val self: ActorRef) extends AnyVal {
    import BBManager._
    def allocate(implicit tm: Timeout): Future[ByteBuffer] = self.ask(Allocate).mapTo[ByteBuffer]
    def free(bb: ByteBuffer): Unit = self ! Deallocate(bb)
  }
  def foreverBB(implicit bbman: BBManagerActorRef, tm: Timeout): Source[ByteBuffer, NotUsed] = {
    def forever: Stream[Future[ByteBuffer]] = bbman.allocate #:: forever
    Source(forever).mapAsync(8)(identity)
  }
  def ferialize(implicit bbman: BBManagerActorRef, tm: Timeout): Flow[File, ByteString, NotUsed] = {
    Flow[File]
      .zip(foreverBB)
      .map {
        case (file, bb) ⇒
          implicit val _bb = bb
          val bs1 = ByteString(file.toPath.toString.toData)
            .tap(ignoring1(bbman.free(bb)))
          val bs2source = FileIO.fromPath(file.toPath)
          Source.single(bs1).concat(bs2source)
      }
      .flatMapConcat(identity)
  }
  class Repactor extends Actor {
    var written: Long = 0L
    def receive: Receive = {
      case bs: ByteString ⇒
        written += bs.length
        sender ! written
    }
  }
  def report(repactor: ActorRef)(implicit tm: Timeout, ec: ExecutionContext): Sink[ByteString, NotUsed] =
    Flow[ByteString]
      .mapAsync(8)(repactor.ask(_).mapTo[Long])
      .conflate(_ + _)
      .to(Sink.foreach(println))

  implicit val akkaTimeout = Timeout(20.seconds)

  def Main(args: Array[String]): Unit = {
    implicit val thebb = ByteBuffer.allocate(20 << 20)
    "helo".toData
    println { thebb.to[String] }
    12L.toData
    println { thebb.to[Long] }
    Array(1.toByte, 2.toByte).toData
    println { thebb.to[Array[Byte]].map(_.toString).mkString(",") }

    val system = ActorSystem("TheBobers")
    implicit val materializer = ActorMaterializer(materializerSettings = None, namePrefix = Some("Lolis"))(system)
    val repactor = system actorOf (Props(classOf[Repactor]), "repactor")
    implicit val bbman: BBManagerActorRef = system actorOf (Props(classOf[BBManager]), "bbman")

    import ExecutionContext.Implicits.global
    val proc: Flow[File, ByteString, NotUsed] = findFiles.async via ferialize.async
    val sink: Sink[ByteString, Future[IOResult]] = FileIO.toPath(Paths.get("/home/nikos/mem.save.xp"))
    val rep: Sink[ByteString, NotUsed] = report(repactor).async
    val run: RunnableGraph[Future[IOResult]] = files.via(proc).alsoTo(rep).toMat(sink)(Keep.right)
    val done: Future[Any] =
      run.run()
//    Future.successful(())
        .recover { case ex ⇒ ex }
    val bye = done.flatMap(r ⇒ system.terminate().map { (r, _) })
    bye.onComplete(r ⇒ println(s"byte: $r"))
    bye.await
  }
}
