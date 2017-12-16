package gv
package codegen

import
  java.io.{
    Reader,
  },
  java.nio.file.{
    Path,
    Paths,
    Files,
  },
  org.jruby.embed.{
    ScriptingContainer,
  },
  lang._

final class Codegen(
  scriptPathname: String,
  destinationDirPathname: String
) extends AnyRef
  with Runnable
{

  def jruby = new ScriptingContainer

  def scriptPath: Path = Paths.get(scriptPathname)
  def destinationPath: Path = Paths.get(destinationDirPathname)

  def inputSource: (Reader, String) = {
    val script = scriptPath
    val reader = Files.newBufferedReader(script)
    (reader, script.toString)
  }

  def apply2[a, b, r](ab: ⇒ (a, b))(f: (a, b) ⇒ r): r =
    f.tupled(ab)

  def run(): Unit = println {
    apply2(inputSource) {
      jruby
        .tap(_ setArgv Array(destinationPath.toString))
        .runScriptlet
    }
  }

}
