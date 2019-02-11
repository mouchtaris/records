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
  scala.language.{
    implicitConversions,
    higherKinds,
  },
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
        .foldLeft(StringBuilder.newBuilder.append(".")) { (builder, elem) ⇒ builder ++= "/" ++= elem }
        .mkString
  }

  object Rb {
    trait Val[T] extends Any {
      final protected[this] def Ind(ind: Int)(str: String): String = s"${"  " * ind}$str"
      def literal(self: T, ind: Int): String
    }
    final class StringVal(val unit: Unit) extends AnyVal with Val[String] {
      private[this] def escape(str: String): String = str.replace("{", "\\{")
      override def literal(self: String, ind: Int): String = s"%q{${escape(self)}}"
    }
    final class TraversableVal[CC[a] <: Traversable[a], T](val ev: Val[T]) extends AnyVal with Val[CC[T]] {
      override def literal(self: CC[T], ind: Int): String = {
        val pre = "["
        val mid = self
          .map {
            el ⇒
              Ind(ind + 1) {
                Rb.Val(ind + 2)(el)(ev)
              }
          }
          .mkString(",\n")
        val aft = Ind(ind)("]")
        s"$pre\n$mid\n$aft"
      }
    }
    final class OptsVal[CC[a] <: Traversable[a], T](val ev: Val[T]) extends AnyVal with Val[CC[(Symbol, T)]] {
      override def literal(self: CC[(Symbol, T)], ind: Int): String =
        self
          .map {
            case (sym, v) ⇒ Ind(ind) {
              s"${sym.name}: ${ev.literal(v, ind)}"
            }
          }
          .mkString(",\n")
    }
    final class OptsMapVal[CC[a, b] <: Traversable[(a, b)], T](val ev: Val[T]) extends AnyVal with Val[CC[Symbol, T]] {
      override def literal(self: CC[Symbol, T], ind: Int): String =
        new OptsVal[Traversable, T](ev).literal(self, ind)
    }

    object Val {
      implicit def stringVal: StringVal = new StringVal(())
      implicit def traversableVal[CC[a] <: Traversable[a], T: Val]: TraversableVal[CC, T] = new TraversableVal(implicitly)
      implicit def optsVal[CC[a] <: Traversable[a], T: Val]: OptsVal[CC, T] = new OptsVal(implicitly)
      implicit def optsMapVal[CC[a, b] <: Traversable[(a, b)], T: Val]: OptsMapVal[CC, T] = new OptsMapVal(implicitly)
      def apply[T: Val](ind: Int)(obj: T): String = implicitly[Val[T]].literal(obj, ind)
      def apply[T: Val](obj: T): String = apply(0)(obj)
      def apply[T: Val]: Val[T] = implicitly
    }

    trait Valable extends Any {
      type T
      def self: T
      def ev: Val[T]
      final def literal(ind: Int): String = Rb.Val(ind)(self)(ev)
    }
    object Valable {
      final case class Impl[T_](
        override val self: T_
      )(
        implicit
        override val ev: Val[T_],
      )
        extends AnyRef
        with Valable
      {
        override type T = T_
      }
      def apply[T: Val](obj: T): Impl[T] = Impl(obj)
      final implicit class ValableVal(val unit: Unit) extends AnyVal with Val[Valable] {
        override def literal(self: Valable, ind: Int): String = self.literal(ind)
      }
      implicit def valableVal: ValableVal = ()
    }
    @deprecated("Use Valable() instead", "0.0.0")
    implicit def closed[T: Val](obj: T): Valable = Valable(obj)
    @deprecated("Use Val.Rb() instead", "0.0.0")
    def str(value: String): String = Rb.Val(value)
  }

  trait RootAdt extends Any {
    def root: JPath
  }
  trait GemEnv extends Any {
    this: RootAdt ⇒
    final def rubyGems: JPath = root resolve "_rubygems"
  }
  trait BundlerEnv extends Any {
    this: RootAdt ⇒
    final def bundlerEnvs: JPath = root resolve "_bundler_envs"
    final def bundlerGems: JPath = root resolve "_bundler_gems"
    final def bundlerVersion: String = "1.17.3"
    final def bundlerGemfile: JPath = bundlerEnvs resolve bundlerEnv resolve "Gemfile"
    def bundlerEnv: String
  }

  final implicit class Env(val self: (JPath, String)) extends AnyVal with RootAdt with GemEnv with BundlerEnv {
    override def root: JPath = self._1
    override def bundlerEnv: String = self._2
  }

  final implicit class Lib(val root: JPath) extends AnyVal {
    def load(path: JPath): Code = getClass.getResourceAsStream((path \ root).toUnix)
    def libexec(name: String): Code = load(root / "libexec" / s"$name.rb")
  }

  object opts {
    val gemHome = Symbol("gem_home")

    val bundleVersion = Symbol("bundle_version")
    val bundleGemfile = Symbol("bundle_gemfile")
    val bundlePath = Symbol("bundle_path")

    val name = Symbol("name")
    val version = Symbol("version")

    val gemName = Symbol("gem_name")
    val bin = Symbol("bin")
    val argv = Symbol("argv")
  }


  final case class Command(name: String, opts: Map[Symbol, Rb.Valable])
  final case class Ruby(
    gemEnv: GemEnv,
    lib: Lib,
    bundlerEnv: Param[BundlerEnv],
  ) {

    def bundlerOpts: Map[Symbol, Rb.Valable] =
      bundlerEnv
        .map { be ⇒
          Map(
            opts.bundleVersion → Rb.Valable(be.bundlerVersion),
            opts.bundleGemfile → Rb.Valable(be.bundlerGemfile.toUnix),
            opts.bundlePath → Rb.Valable(be.bundlerGems.toUnix),
          )
        }
        .getOrElse(Map.empty)

    def gemOpts: Map[Symbol, Rb.Valable] =
      Map(
        opts.gemHome → Rb.Valable(gemEnv.rubyGems.toUnix)
      )

    def toCode(command: Command): String = command match {
      case Command(name, opts) ⇒
        val newOpts = Rb.Val(2)(gemOpts ++ bundlerOpts)
        val optsStr = Rb.Val(2)(opts)
        s"""
           |require 'ruby_layer'
           |
           |RubyLayer
           |  .new(
           |$newOpts
           |  )
           |  .$name(
           |$optsStr
           |  )
           |""".stripMargin
    }

    def install_gem(name: String, version: Param[String]) = Command(
      name = "install_gem",
      opts = version
        .map(opts.version → _)
        .foldLeft(Map(opts.name → name))(_ + _)
        .map { case (sym, str) ⇒ sym → Rb.closed(str) }
    )

    def irb = Command(
      name = "irb_session",
      opts = Map.empty,
    )

    def gem_bin_stub(gemName: String, bin: String, args: Vector[String]) = Command(
      name = "gem_bin_stub",
      opts = Map(
        opts.gemName → gemName,
        opts.bin → bin,
        opts.argv → args,
      )
    )
  }


}

final case class Hatchy() {
}
