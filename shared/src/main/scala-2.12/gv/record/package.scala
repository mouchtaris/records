package gv

import
  lang._,
  list._,
  list.op._

package object record {

  final implicit class GhostlyRecordDecoration[
    fields <: List,
  ](
    val self: Record.of[fields]
  )
    extends AnyVal
  {

    def evidence[vals <: List: self.Evidence](vals: vals): self.Evidence[vals] = implicitly

    def +[f2 <: List, f <: List: concat.resultOf[fields, f2]#λ](r2: Record.of[f2]): Concatenation[self.type, r2.type, f] =
      new Concatenation(self, r2)
  }

  final implicit class ListDecoration[
    vals <: List,
  ](
    val self: vals
  )
    extends AnyVal
  {
    def got[r <: Record](n: r#ScopedNamed[_])(implicit ev: record.Evidence[r#Fields, vals]): n.T =
      ev.getters(n)(self).asInstanceOf[n.T]
  }

  final implicit class ImplicitFields[
    fields <: List,
  ](
    val self: fields
  )
    extends AnyVal

  def implicitlyFields[fields <: List: ImplicitFields]: fields = implicitly[ImplicitFields[fields]].self

  implicit val nilImplicitFields: ImplicitFields[Nil] = Nil

  implicit def listImplicitFields[
    h: Implicitly,
    t <: List: ImplicitFields
  ]: ImplicitFields[h :: t] =
    implicitly[h] :: implicitlyFields[t]

}
