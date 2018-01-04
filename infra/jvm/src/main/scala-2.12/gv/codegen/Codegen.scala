package gv
package codegen

import
  java.io.{
    Reader,
    InputStreamReader,
    BufferedInputStream,
  },
  java.nio.file.{
    Path,
    Paths,
  },
  java.nio.charset.StandardCharsets.{
    UTF_8,
  },
  scala.concurrent.{
    Future,
    Await,
  },
  scala.concurrent.duration._,
  scala.util.{
    Try,
    Success,
    Failure,
  },
  scala.reflect.{
    ClassTag,
    classTag,
  },
  org.jruby.embed.{
    ScriptingContainer,
  },
  akka.{
    Done,
    NotUsed,
  },
  akka.stream.{
    Materializer,
    FlowShape,
  },
  akka.stream.scaladsl.{
    Source,
    Sink,
    Flow,
    GraphDSL,
    Broadcast,
    Merge,
  },
  lang._

object Codegen {

  lazy val scriptPath: Path = Paths.get("/jruby/codegen.rb")

  def resource(path: String) =
    new BufferedInputStream(
      getClass.getResourceAsStream(path),
      1 << 10
    )

  def resourceReader(path: String) =
    new InputStreamReader(
      resource(path),
      UTF_8
    )

  def inputSource: (Reader, String) = {
    val script = scriptPath.toString
    val reader = resourceReader(script)
    (reader, script)
  }

  def apply2[a, b, r](ab: ⇒ (a, b))(f: (a, b) ⇒ r): r =
    f.tupled(ab)

  final implicit class ToRubyIterableDecoration(val self: Object) extends AnyVal {
    def toRubyIterable(implicit facade: Facade) =
      RubyIterable(facade, self)
  }

  final case class RubyIterable(facade: Facade, iterable: Object)
    extends scala.collection.AbstractIterator[Object]
  {
    def hasNext: Boolean =
      facade.engine.callMethod(iterable, "next?") match {
        case b: java.lang.Boolean ⇒ b.booleanValue()
        case _ ⇒ false
      }
    def next(): Object =
      facade.engine.callMethod(iterable, "next!")
  }

  sealed trait TemplateHandler extends AnyRef {
    def facade: Facade
    def handler: Object

    def handle(): Unit =
      facade.engine.callMethod(handler, "handle!")

    val template: String =
      facade.engine.callMethod(handler, "template").toString
  }

  final case class InfestationHandler(handler: Object)(implicit val facade: Facade)
    extends AnyRef
    with TemplateHandler

  final case class PlainHandler(handler: Object)(implicit val facade: Facade)
    extends AnyRef
    with TemplateHandler

  final case class Facade(engine: ScriptingContainer, facade: Object) {
    implicit val implicitFacade: Facade = this

    def handlers[T](typ: String)(wrap: Object ⇒ T): Stream[T] =
      engine
        .callMethod(facade, s"${typ}_iterable")
        .toRubyIterable
        .toStream
        .map(wrap)

    def infestations: Stream[InfestationHandler] =
      handlers("infestations")(InfestationHandler(_))

    def plain: Stream[PlainHandler] =
      handlers("plain")(PlainHandler(_))
  }

  val getFacadeFunction: ScriptingContainer ⇒ Facade = { jruby ⇒
      apply2(inputSource)(jruby.runScriptlet)
      val facade = jruby.get("Facade")
      Facade(jruby, facade)
    }

  val getFacade: Flow[ScriptingContainer, Facade, NotUsed] =
    Flow fromFunction getFacadeFunction

  val getInfestations: Flow[Facade, InfestationHandler, NotUsed] =
    Flow
      .fromFunction((_: Facade).infestations)
      .flatMapMerge(8, Source.apply)

  val getPlain: Flow[Facade, PlainHandler, NotUsed] =
    Flow
      .fromFunction((_: Facade).plain)
      .flatMapMerge(8, Source.apply)

  val getHandlers: Flow[Facade, TemplateHandler, NotUsed] =
    Flow fromGraph GraphDSL.create(
      getInfestations.async,
      getPlain.async,
    )((_, _) ⇒ NotUsed) { implicit b ⇒ (infst, plain) ⇒
      import GraphDSL.Implicits._
      val bcast = b add Broadcast[Facade](2)
      val merge = b add Merge[TemplateHandler](2)
      bcast.out(0) ~> infst.in
                      infst.out ~> merge.in(0)
      bcast.out(1) ~> plain.in
                      plain.out ~> merge.in(1)
      FlowShape(bcast.in, merge.out)
    }

  val runHandler: Flow[TemplateHandler, TemplateHandler, NotUsed] = Flow fromFunction {
    (_: TemplateHandler) tap (_ handle ())
  }

  val process: Flow[ScriptingContainer, TemplateHandler, NotUsed] =
    getFacade.async
      .via(getHandlers).async
      .via(runHandler).async

  val recovery: Flow[TemplateHandler, Try[TemplateHandler], NotUsed] =
    Flow
      .fromFunction { Success(_: TemplateHandler): Try[TemplateHandler] }
      .recover { case ex ⇒ Failure(ex) }
      .async

  val handleTemplate: Sink[Try[TemplateHandler], Future[Vector[Try[TemplateHandler]]]] =
    Sink.collection[Try[TemplateHandler], Vector[Try[TemplateHandler]]]

  def processTemplates(jruby: ScriptingContainer)(implicit mat: Materializer): Future[Vector[Try[TemplateHandler]]] =
    Source single jruby via process via recovery runWith handleTemplate

  val MAX_WAIT = 10.seconds
}

final class Codegen(
  manifestPathname: String,
  destinationDirPathname: String
)(
  implicit
  materializer: Materializer
) extends AnyRef
  with Runnable
{

  val argv = Array(
    manifestPathname,
    destinationDirPathname
  )

  val jruby = new ScriptingContainer()
    .tap(_ setArgv argv)

  def run(): Unit = println {
    import Console._
    Await
      .result(
        Codegen.processTemplates(jruby),
        Codegen.MAX_WAIT
      )
      .map {
        case Success(han) ⇒
          s"[$GREEN OK$RESET ] ${han.template}"
        case Failure(ex) ⇒
          s"[$RED FAIL$RESET ] $ex\n${ex.stackTrace}"
      }
      .mkString("\n")
  }

}
