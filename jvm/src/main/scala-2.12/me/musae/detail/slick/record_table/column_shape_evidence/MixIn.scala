package me.musae
package detail.slick
package record_table
package column_shape_evidence

trait MixIn extends Any {
  this: Tables ⇒
  import profile.api._

  sealed trait ColumnsShapeEvidence[columns] {

    final type λ[shape] =
      Shape[_ <: FlatShapeLevel, columns, shape, _]

  }

}
