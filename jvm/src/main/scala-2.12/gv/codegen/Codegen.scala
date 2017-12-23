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
  org.jruby.embed.{
    ScriptingContainer,
  },
  akka.{ Done, NotUsed },
  akka.stream.{ Materializer },
  akka.stream.scaladsl.{ Source, Sink, Flow },
  lang._,
  fun._

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

  val getFacade: Flow[ScriptingContainer, Facade, NotUsed] = Flow
    .fromFunction { jruby ⇒
      val facade = apply2(inputSource)(jruby.runScriptlet)
      Facade(jruby, facade)
    }

  val getTemplates: Flow[Facade, Template, NotUsed] = Flow[Facade]
    .map(_.templates)
    .flatMapMerge(8, Source.apply)

  val getHandlers: Flow[Template, Handler, NotUsed] = Flow[Template]
    .map(_.handlers)
    .flatMapMerge(8, Source.apply)

  val process: Flow[ScriptingContainer, Handler, NotUsed] = Flow[ScriptingContainer]
    .via(getFacade).async
    .via(getTemplates).async
    .via(getHandlers).async

  val handleTemplate: Sink[Handler, Future[Done]] = Sink
    .foreach(_.handle())

  def processTemplates(jruby: ScriptingContainer)(implicit mat: Materializer): Future[Done] =
    Source single jruby via process runWith handleTemplate

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
    Await.result(
      Codegen.processTemplates(jruby),
      Codegen.MAX_WAIT
    )
  }

}
