package yc
package ph
package adt

final class Examination(val exams: Set[Exam]) extends AnyVal

object Examination {

  def apply(exams: Exam*) = new Examination(exams.toSet)
}