package yc
package ph
package bs

import scala.language.higherKinds

object bsjson {

  trait JsVal extends Any { override def toString: String }
  trait ToVal[T] extends Any { def apply(obj: T): JsVal }
  object ToVal {
    def apply[T: ToVal](obj: T): JsVal = implicitly[ToVal[T]].apply(obj)
  }
  implicit val longToVal: ToVal[Long] = new JsNum(_)
  implicit val strToVal: ToVal[String] = new JsStr(_)
  implicit def travToVal[CC[a] <: Traversable[a], T: ToVal]: ToVal[CC[T]] = trav ⇒ new JsArr(trav.map(ToVal(_)).toVector)
  implicit def pairsToVal[CC[a] <: Traversable[a], A: ToVal, B: ToVal]: ToVal[CC[(A, B)]] =
    trav ⇒ new JsMap(trav.map { case (a, b) ⇒ (ToVal(a), ToVal(b)) }.toMap)
  implicit def mapToVal[CC[a, b] <: Map[a, b], A: ToVal, B: ToVal]: ToVal[CC[A, B]] = map ⇒ pairsToVal[Stream, A, B].apply(map.toStream)
  implicit val valToVal: ToVal[JsVal] = identity _

  object JsVal {
    def apply[T: ToVal](obj: T): JsVal = ToVal(obj)

    val score = apply("score")
    val comments = apply("comments")
    val metainformation = apply("metainformation")
    def apply(value: adt.ExamReport): JsVal = apply(Vector(
      score → apply(value.score),
      comments → apply(value.comments),
      metainformation → apply(value.metainformation),
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