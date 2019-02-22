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

  implicit val examReport: ToVal[adt.ExamReport] = r ⇒ ToVal(Vector(
    "score" → JsVal(r.score),
    "comments" → JsVal(r.comments),
    "metainformation" → JsVal(r.metainformation),
    "max" → JsVal(r.max),
  ))
  implicit val examId: ToVal[adt.ExamId] = id ⇒ ToVal(id.value)

  implicit val component: ToVal[adt.Component] = c ⇒ ToVal(Vector(
    "projectId" → JsVal(c.projectId.value)
  ))

  implicit val componentInstance: ToVal[adt.ComponentInstance] = i ⇒ ToVal(Vector(
    "at" → JsVal(i.at.getEpochSecond),
    "commit" → JsVal(i.commit.toString),
    "component" → JsVal(i.component),
  ))

  implicit val componentReport: ToVal[adt.ComponentReport] = r ⇒ ToVal(Vector(
    "exams" → JsVal(r.exams),
    "overall" → JsVal(r.overall),
    "possible_max" → JsVal(r.possibleMax),
  ))

  object JsVal {
    def apply[T: ToVal](obj: T): JsVal = ToVal(obj)
  }

  final class JsNum(val value: Long) extends AnyVal with JsVal {
    override def toString: String = value.toString
  }
  object JsNum {
    def unapply(v: JsVal): Option[Long] = v match {
      case num: JsNum ⇒ Some(num.value)
      case _ ⇒ None
    }
  }

  final class JsStr(val value: String) extends AnyVal with JsVal {
    override def toString: String = s""""${
      value.replaceAll("\\\"", "\\\"")
    }""""
  }
  object JsStr {
    def unapply(v: JsVal): Option[String] = v match {
      case str: JsStr ⇒ Some(str.value)
      case _ ⇒ None
    }
  }

  final class JsArr(val value: Vector[JsVal]) extends AnyVal with JsVal {
    override def toString: String = s"[${value.map(_.toString).mkString(", ")}]"
  }
  object JsArr {
    def unapply(v: JsVal): Option[Vector[JsVal]] = v match {
      case arr: JsArr ⇒ Some(arr.value)
      case _ ⇒ None
    }
  }

  final class JsMap(val value: Map[JsVal, JsVal]) extends AnyVal with JsVal {
    override def toString: String = s"{${value.map { case (k, v) ⇒ s"$k => $v" }.mkString(", ")}}"
  }
  object JsMap {
    def unapply(v: JsVal): Option[Map[JsVal, JsVal]] = v match {
      case map: JsMap ⇒ Some(map.value)
      case _ ⇒ None
    }
  }

}
