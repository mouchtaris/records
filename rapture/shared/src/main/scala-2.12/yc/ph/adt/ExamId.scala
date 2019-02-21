package yc
package ph
package adt

final class ExamId(val value: String) extends AnyVal {
  override def toString: String = s"ExamId[$value]"
}

object ExamId {

  def apply(value: String) = new ExamId(value)

}
