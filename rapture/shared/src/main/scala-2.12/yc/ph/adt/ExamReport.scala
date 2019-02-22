package yc
package ph
package adt

final case class ExamReport(
  score: Long,
  max: Long,
  comments: Vector[String] = Vector.empty,
  metainformation: Map[String, String] = Map.empty,
)
  extends AnyRef

