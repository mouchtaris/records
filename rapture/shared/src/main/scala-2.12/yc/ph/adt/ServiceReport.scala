package yc
package ph
package adt

object ServiceReport {

  type Value = Map[ExamId, ExamReport]

  object Value {
    def empty: Value = Map.empty
  }

  def apply(value: Value) = new ServiceReport(value)

}

final class ServiceReport(val value: ServiceReport.Value = ServiceReport.Value.empty) extends AnyVal