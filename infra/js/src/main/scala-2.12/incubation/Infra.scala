package incubation

import
  scalajs.js,
  scalajs.js.annotation._

@JSExportTopLevel("infra")
@JSExportAll
object Infra {

  def poo = infra.AntePaliRecordsLib.tests.poo

  def vargs(args: js.Object*): js.Array[js.Object] = js.Array(args: _*)

  //class FromJs(rec: Record) {
  //
  //  trait MakeField
  //  implicit def definedMakeField[
  //    f <: rec.ScopedNamed[_],
  //  ](
  //    implicit
  //    f: f
  //  ): Defined[MakeField, f, Any ⇒ f.T] =
  //    ???
  //
  //
  //}


}
