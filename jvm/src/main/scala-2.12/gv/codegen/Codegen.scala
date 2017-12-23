package gv
package codegen

import
  java.io.{
    Reader,
    InputStreamReader,
  },
  java.nio.file.{
    Path,
    Paths,
    Files,
  },
  java.nio.charset.StandardCharsets.{
    UTF_8,
  },
  org.jruby.embed.{
    ScriptingContainer,
  },
  lang._

object Codegen {

  def scriptPath: Path = Paths.get("/jruby/codegen.rb")

  def resource(path: String) =
    getClass.getResourceAsStream(path)
}

final class Codegen(
  inputDirPathname: String,
  destinationDirPathname: String
) extends AnyRef
  with Runnable
{

  def argv = Array(
    inputDirPathname,
    destinationDirPathname
  )

  def jruby = new ScriptingContainer

  def inputSource: (Reader, String) = {
    import Codegen.scriptPath
    val script = scriptPath.toString
    val reader = new InputStreamReader(getClass.getResourceAsStream(script), UTF_8)
    (reader, script)
  }

  def apply2[a, b, r](ab: ⇒ (a, b))(f: (a, b) ⇒ r): r =
    f.tupled(ab)

  def run(): Unit = println {
    apply2(inputSource) {
      jruby
        .tap(_ setArgv argv)
        .runScriptlet
    }
  }

}
