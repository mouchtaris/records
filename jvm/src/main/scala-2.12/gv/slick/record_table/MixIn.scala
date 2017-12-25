package gv
package slick
package record_table

import
  scala.reflect.{
    ClassTag,
  },
  list._,
  list.op._,
  record._

trait MixIn
  extends AnyRef
  with column_shape_evidence.MixIn
  with record_columns.MixIn
{
  val profile: _root_.slick.jdbc.JdbcProfile
  import profile.api._

  final class RecordTable[
    record <: Record.of[fields],
    fields <: List,
    columnsList <: List,
    columns,
    provenColumnsShape,
    provenColumnsList <: List,
    tagged <: List: ClassTag,
    provenMappedColumnsShape,
  ](
    rec: record,
    tag: Tag
  )(
    implicit
    recordColumns: RecordColumns[fields, columnsList],
    recordColumnsListToTuple: ToTuple[columnsList, columns],
    columnsShape: ColumnsShapeEvidence[columns]#λ[provenColumnsShape],
    provenColumnsToList: ToList[provenColumnsShape, provenColumnsList],
    tagged: record.untagged.FromUntagged[fields, provenColumnsList, tagged],
    untagged: record.untagged.ToUntagged[tagged, provenColumnsList],
    provenColumnsToTuple: ToTuple[provenColumnsList, provenColumnsShape],
    mappedColumnsShape: MappedColumnsShapeEvidence[provenColumnsShape, tagged]#λ[provenMappedColumnsShape],
  )
    extends Table[provenMappedColumnsShape](tag, rec.name)
  {

    type Fields = fields

    def col[n <: rec.ScopedNamed[_], v](n: n)(
      implicit
      get: kvget.Evidence[Fields, columnsList, n, v]
    ): v =
      get(columns)

    lazy val columns: columnsList =
      recordColumns(this)

    lazy val * : ProvenShape[provenMappedColumnsShape] =
      ProvenShape.proveShapeOf {
        val toTagged: provenColumnsShape ⇒ tagged = c ⇒ rec.fromUntagged(c.toList)
        val fromTagged: tagged ⇒ Option[provenColumnsShape] = l ⇒ Some(untagged(l).toTuple)
        columns.toTuple <> (toTagged, fromTagged)
      }(mappedColumnsShape)

  }

  final type AnyRecordTable[r <: Record, columnsList <: List] = RecordTable[
    r,
    _ <: List,
    columnsList,
    _,
    _,
    _ <: List,
    _ <: List,
    _,
  ]

  final def RecordTable[
    record <: Record,
    columnsList <: List,
    columns,
    provenColumnsShape,
    provenColumnsList <: List,
    tagged <: List: ClassTag,
    provenMappedColumnsShape,
  ](rec: record)(tag: Tag)(
    implicit
    recordColumns: RecordColumns[rec.Fields, columnsList],
    recordColumnsListToTuple: ToTuple[columnsList, columns],
    columnsShape: ColumnsShapeEvidence[columns]#λ[provenColumnsShape],
    provenColumnsToList: ToList[provenColumnsShape, provenColumnsList],
    tagged: record.untagged.FromUntagged[rec.Fields, provenColumnsList, tagged],
    untagged: record.untagged.ToUntagged[tagged, provenColumnsList],
    provenColumnsToTuple: ToTuple[provenColumnsList, provenColumnsShape],
    mappedColumnsShape: MappedColumnsShapeEvidence[provenColumnsShape, tagged]#λ[provenMappedColumnsShape],
  ) =
    new RecordTable[
      rec.type,
      rec.Fields,
      columnsList,
      columns,
      provenColumnsShape,
      provenColumnsList,
      tagged,
      provenMappedColumnsShape,
    ](rec, tag)

}
