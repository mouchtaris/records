package yc
package ph
package bs

object bsjson {

  trait JsVal extends Any { override def toString: String }

  object JsVal {
    def apply(value: Long) = new JsNum(value)
    def apply(value: String) = new JsStr(value)
    def apply(value: Vector[JsVal]) = new JsArr(value)
    def apply(value: Vector[(JsVal, JsVal)]) = new JsMap(value.toMap)

    val score = apply("score")
    val comments = apply("comments")
    def apply(value: adt.ExamReport): JsVal = apply(Vector(
      score → apply(value.score),
      comments → apply(value.comments.map(apply))
    ))
    def apply(examId: adt.ExamId): JsVal = apply(examId.value)

    val projectId = apply("projectId")
    def apply(service: adt.Service): JsVal = apply(Vector(
      projectId → apply(service.projectId.value),
    ))

    val at = apply("at")
    val service = apply("service")
    val commit = apply("commit")
    def apply(serviceInstance: adt.ServiceInstance): JsVal = apply(Vector(
      service → apply(serviceInstance.service),
      at → apply(serviceInstance.at.getEpochSecond),
      commit → apply(serviceInstance.commit.toString),
    ))

    def apply(serviceReport: adt.ServiceReport): JsVal = apply {
      serviceReport.value
        .map { case (examId, examReport) ⇒ JsVal(examId) → JsVal(examReport) }
        .toVector
    }
  }

  final class JsNum(val value: Long) extends AnyVal with JsVal {
    override def toString: String = value.toString
  }

  final class JsStr(val value: String) extends AnyVal with JsVal {
    override def toString: String = s""""${
      value.replaceAll("\\\"", "\\\"")
    }""""
  }

  final class JsArr(val value: Vector[JsVal]) extends AnyVal with JsVal {
    override def toString: String = s"[${value.map(_.toString).mkString(", ")}]"
  }

  final class JsMap(val value: Map[JsVal, JsVal]) extends AnyVal with JsVal {
    override def toString: String = s"{${value.map { case (k, v) ⇒ s"$k => $v" }.mkString(", ")}}"
  }

}
