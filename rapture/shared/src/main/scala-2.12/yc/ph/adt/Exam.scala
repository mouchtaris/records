package yc
package ph
package adt

trait Exam extends Any {

  def id: ExamId

  def apply(inst: ServiceInstance): ExamReport

}
