package gv
package slick
package record_table
package column_shape_evidence

trait MixIn extends Any {
  this: record_table.MixIn ⇒
  import profile.api._

  sealed trait ColumnsShapeEvidence[columns] {
    final type λ[shape] =
      Shape[_ <: FlatShapeLevel, columns, shape, _]
  }

  sealed trait MappedColumnsShapeEvidence[columns, mapped] {
    final type λ[shape] =
      Shape[_ <: FlatShapeLevel, MappedProjection[mapped, columns], shape, _]
  }

}
