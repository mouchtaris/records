package incubation

import
  scalajs.js.annotation._,
  gv.{
    list,
    record,
    fun,
  },
  list._,
  record._,
  fun._,
  infra.blue.model

@JSExportTopLevel("infra")
@JSExportAll
object Infra {

  class FromJs(rec: Record) {

    trait MakeField
    implicit def definedMakeField[
      f <: rec.ScopedNamed[_],
    ](
      implicit
      f: f
    ): Defined[MakeField, f, Any ⇒ f.T] =
      ???


  }


}
