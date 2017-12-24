package me.musae
package detail.slick
package record_table
package record_columns

trait MixIn extends Any {
  this: Tables ⇒
  import profile.api._

  sealed trait RecordColumns[
    fields <: List,
    columns <: List,
  ] {
    def apply(table: Table[_]): columns
  }

  final implicit def nilRecordColumns: RecordColumns[Nil, Nil] =
    new RecordColumns[Nil, Nil] {
      def apply(table: Table[_]): Nil =
        Nil
    }

  final implicit def listRecordColumns[
    n <: Named[_],
    t <: List,
    tcols <: List,
  ](
    implicit
    tcols: RecordColumns[t, tcols],
    nev: n#BaseEvidence[TypedType],
    n: n,
  ): RecordColumns[n :: t, Rep[nev.T] :: tcols] =
    new RecordColumns[n :: t, Rep[nev.T] :: tcols] {
      def apply(table: Table[_]): Rep[nev.T] :: tcols = {
        val h: Rep[nev.T] = table.column[nev.T](n.name)(nev.ev)
        val t: tcols = tcols(table)
        h :: t
      }
    }

}