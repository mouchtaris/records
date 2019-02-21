package yc
package ph
package adt

final case class ExamReport(
  score: Long = 0,
  comments: Vector[String] = Vector.empty,
)
  extends AnyRef
{
  override def toString: String =
    s"ExamReport[score=$score, comments=[${comments.mkString(", ")}]"
}
