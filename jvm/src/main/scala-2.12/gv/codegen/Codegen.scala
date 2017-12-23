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
  scala.util.{ Try, Success, Failure },
  org.jruby.embed.{
    ScriptingContainer,
  },
  akka.{ Done, NotUsed },
  akka.stream.{ Materializer },
  akka.stream.scaladsl.{ Source, Sink, Flow },
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

  final case class Handler(template: Template, handler: Object) {
    def handle(): Unit =
      template.facade.engine.callMethod(handler, "handle!")
  }

  final case class Template(facade: Facade, template: Object) {
    def handlers: Stream[Handler] =
      facade
        .handlers_for(template)
        .map(Handler(this, _))
        .toStream
  }

  final case class Facade(engine: ScriptingContainer, facade: Object) {
    def templates: Stream[Template] =
      engine
        .callMethod(facade, "templates_java", classOf[Array[Object]])
        .toStream
        .map(Template(this, _))

    def handlers_for(template: Object): Array[Object] =
      engine
        .callMethod(facade, "handlers_for_java", Array(template), classOf[Array[Object]])
  }

  val getFacade: Flow[ScriptingContainer, Facade, NotUsed] =
    Flow fromFunction { jruby ⇒
      apply2(inputSource)(jruby.runScriptlet)
      val facade = jruby.get("Facade")
      Facade(jruby, facade)
    }

  val getTemplates: Flow[Facade, Template, NotUsed] =
    Flow
      .fromFunction((_: Facade).templates)
      .flatMapMerge(8, Source.apply)

  val getHandlers: Flow[Template, Handler, NotUsed] =
    Flow
      .fromFunction((_: Template).handlers)
      .flatMapMerge(8, Source.apply)

  val runHandler: Flow[Handler, Handler, NotUsed] = Flow fromFunction {
    (_: Handler) tap (_ handle ())
  }

  val process: Flow[ScriptingContainer, Handler, NotUsed] =
    getFacade.async
      .via(getTemplates).async
      .via(getHandlers).async
      .via(runHandler).async

  val recovery: Flow[Handler, Try[Handler], NotUsed] =
    Flow
      .fromFunction { Success(_: Handler): Try[Handler] }
      .recover { case ex ⇒ Failure(ex) }
      .async

  val handleTemplate: Sink[Try[Handler], Future[Vector[Try[Handler]]]] =
    Sink.fold(Vector.empty[Try[Handler]])(_ :+ _)

  def processTemplates(jruby: ScriptingContainer)(implicit mat: Materializer): Future[Vector[Try[Handler]]] =
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
          s"[$GREEN OK$RESET ] ${han.template.template}"
        case Failure(ex) ⇒
          s"[$RED FAIL$RESET ] $ex"
      }
      .mkString("\n")
  }

}
