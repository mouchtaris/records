package me.musae
package detail.slick
package record_table

import
  gv.{
    list,
  },
  list.{
    ToTuple,
  },
  list.op._

trait MixIn
  extends Any
  with column_shape_evidence.MixIn
  with record_columns.MixIn
{
  this: Tables ⇒
  import profile.api._

  final class RecordTable[
    record <: Record.of[fields],
    fields <: List,
    columnsList <: List,
    columns,
    provenColumnsShape,
  ](
    rec: record,
    tag: Tag
  )(
    implicit
    recordColumns: RecordColumns[fields, columnsList],
    recordColumnsListToTuple: ToTuple[columnsList, columns],
    columnsShape: ColumnsShapeEvidence[columns]#λ[provenColumnsShape],
  )
    extends Table[provenColumnsShape](tag, rec.name)
  {

    type Fields = fields

    def col[n <: rec.ScopedNamed[_], v](n: n)(
      implicit
      get: kvget.Evidence[Fields, columnsList, n, v]
    ): v =
      get(columns)

    lazy val columns: columnsList =
      recordColumns(this)

    lazy val * : ProvenShape[provenColumnsShape] =
      ProvenShape.proveShapeOf(columns.toTuple)(columnsShape)

  }

  final def RecordTable[
    record <: Record,
    columnsList <: List,
    columns,
    provenColumnsShape,
  ](rec: record)(tag: Tag)(
    implicit
    recordColumns: RecordColumns[rec.Fields, columnsList],
    recordColumnsListToTuple: ToTuple[columnsList, columns],
    columnsShape: ColumnsShapeEvidence[columns]#λ[provenColumnsShape],
  ): RecordTable[
    rec.type,
    rec.Fields,
    columnsList,
    columns,
    provenColumnsShape,
  ] =
    new RecordTable[
      rec.type,
      rec.Fields,
      columnsList,
      columns,
      provenColumnsShape,
    ](rec, tag)

}
