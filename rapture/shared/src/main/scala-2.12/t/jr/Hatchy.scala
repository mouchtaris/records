package t
package jr

import
  java.io.{
    InputStream,
    ByteArrayInputStream,
    SequenceInputStream,
  },
  java.net.{
    URI,
  },
  java.nio.{
    CharBuffer,
    ByteBuffer,
  },
  java.nio.charset.{
    CharsetEncoder,
    CharsetDecoder,
    CoderResult,
  },
  java.nio.charset.StandardCharsets.{
    UTF_8,
  },
  java.nio.file.{
    Path ⇒ JPath,
    FileSystem ⇒ JFS,
    FileSystems ⇒ JFSs,
    Paths ⇒ JPaths,
  },
  scala.language.implicitConversions,
  scala.util.{
    Try,
    Success,
    Failure,
  },
  scala.collection.JavaConverters.{
    asScalaIterator,
    asJavaEnumeration,
  },
  scala.collection.{
    mutable,
    immutable,
    IterableLike,
    TraversableOnce,
  },
  immutable._,
  fn.pf.{ Def, Definition, Pf }


object Hatchy {
  val rc: JPath = JPaths get "rapture/jvm/src/main/resources"

  implicit def stringToInputStream(str: String): InputStream =
    new ByteArrayInputStream(str.getBytes(UTF_8))
  implicit def pathToUri(path: JPath): URI =
    URI.create(s"uri:classloader:${path.toUnix}")
  implicit def concat(streams: Seq[InputStream]): InputStream =
    new SequenceInputStream(asJavaEnumeration(streams.iterator))

  type Code = InputStream

  final implicit class PathDecoration(val self: JPath) extends AnyVal {
    def /(other: String): JPath = self resolve other
    def \(from: JPath): JPath = from.self.relativize(self)
    def iterator: Iterator[JPath] = asScalaIterator(self.iterator())
    def toUnix: String =
      iterator
        .map(_.toString)
        .foldLeft(StringBuilder.newBuilder) { (builder, elem) ⇒ builder ++= "/" ++= elem }
        .mkString
  }

  object Rb {
    def str(value: String): String = s"%q{$value}"
  }

  trait RootAdt extends Any {
    def root: JPath
  }
  trait GemEnv extends Any {
    this: RootAdt ⇒
    def rubyGems: JPath = root resolve "_rubygems"
  }

  final implicit class GemEnv(val root: JPath) extends AnyVal with RootAdt with GemEnv

  final implicit class Lib(val root: JPath) extends AnyVal {
    def load(path: JPath): Code = getClass.getResourceAsStream((path \ root).toUnix)
    def libexec(name: String): Code = load(root / "libexec" / s"$name.rb")
  }

  final case class Command(name: String, opts: Map[String, String])
  final case class Ruby(
    gemEnv: GemEnv,
    lib: Lib,
  ) {

    def toCode(command: Command): String = command match {
      case Command(name, opts) ⇒
        val optsStr = opts
          .map { case (a, b) ⇒ s"    $a: ${Rb.str(b)}" }
          .mkString(",\n")
        val gemHomeStr = Rb.str(gemEnv.rubyGems.toUnix)
        s"""
           |require 'ruby_layer'
           |
           |RubyLayer
           |  .new(
           |    gem_home: $gemHomeStr
           |  )
           |  .$name(
           |$optsStr
           |  )
           |""".stripMargin
    }

    def install_gem(name: String, version: Option[String]) = Command(
      name = "install_gem",
      opts = version.map("version" → _).foldLeft(Map("name" → name))(_ + _)
    )

    def irb = Command(
      name = "irb_session",
      opts = Map.empty,
    )
  }


}

final case class Hatchy() {
}
