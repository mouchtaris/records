package mse2

import scala.collection.immutable._

import akka.NotUsed
import akka.stream.scaladsl.Source
import akka.util.ByteString

object Json {

  object Value {
    final type JsonStream = Source[ByteString, NotUsed]

    object Indent {
      val source: JsonStream = Source.single(ByteString(""))
      val indent: JsonStream = Source.single(ByteString("    "))

      val zero = apply(None)
    }

    final case class Indent private (parent: Option[Indent]) {
      import Indent.indent

      val source: JsonStream =
        parent
          .map(_.source)
          .map(_.concat(indent))
          .getOrElse(Indent.source)

      def apply(jsonStr: JsonStream): JsonStream =
        source concat jsonStr

      def + = new Indent(Some(this))
    }


    def str(v: String): JsonStream =
      Source single ByteString(v)

    val commanl: JsonStream = str(",\n")
  }

  sealed trait Value {
    final type JsonStream = Value.JsonStream
    final type Indent = Value.Indent

    def toJson(ind: Indent): JsonStream

    final def toJson: JsonStream = toJson(Value.Indent.zero)
  }

  trait JValue[T] extends Any {
    this: Any
      with Value
    ⇒
    import Value.str
    protected[this] def jValueValue: T

    final def toJson(ind: Indent): JsonStream =
      str(jValueValue.toString)
  }

  final case object JNull extends Value with JValue[String] {
    val jValueValue = "\"null\""
  }

  final case class JNum(jValueValue: Long) extends Value with JValue[Long]

  final case class JBool(jValueValue: Boolean) extends Value with JValue[Boolean]

  final case class JStr(jValueValue: String) extends Value {
    import Value.str
    def toJson(ind: Indent): JsonStream = str(s""""$jValueValue"""")
  }

  final case class JArr(jValueValue: Seq[Value]) extends Value {
    import Value.{ str, commanl, Indent }

    def toJson(ind: Indent): JsonStream =
      str("[\n")
        .concat {
          Source.fromIterator { () ⇒
            val ind2 = ind.+
            jValueValue.iterator
              .map(_.toJson(ind2))
              .map(ind2(_))
              .map(_.concat(commanl))
            }
            .flatMapConcat(identity[JsonStream])
        }
        .concat(ind { str("]") })
  }

  final case class JObj(jValueValue: Map[String, Value]) extends Value {
    import Value.{ str, commanl }

    def toJson(ind: Indent): JsonStream =
      str("{\n")
        .concat {
          Source.fromIterator { () ⇒
            jValueValue.iterator.map {
              case (name, value) ⇒
                val ind2 = ind.+
                ind2 { JStr(name).toJson(ind2) }
                  .concat(str(": "))
                  .concat(value.toJson(ind2))
                  .concat(commanl)
              }
          }
          .flatMapConcat(identity[JsonStream])
        }
        .concat(ind { str("}") })
  }

  trait ToJson[T] extends Any {
    def apply(obj: T): Value
  }

  implicit val jsonValueToJson: ToJson[Value] = identity[Value]
  implicit val strToJson: ToJson[String] = JStr(_)
  implicit val boolToJson: ToJson[Boolean] = JBool(_)
  implicit val intToJson: ToJson[Long] = JNum(_)
  implicit val nullToJson: ToJson[Null] = _ ⇒ JNull
  implicit val seqToJson: ToJson[Seq[Value]] = JArr(_)
  implicit val mapToJson: ToJson[Map[String, Value]] = JObj(_)

  final implicit class ToJsonDecoration[T](val self: T) extends AnyVal {
    def toJson(implicit json: ToJson[T]): Value = json(self)
  }

  def test(): Value = {
    JObj {
      Map(
        "bolis" → JArr {
          Vector(
            JNull,
            JNum(12),
            JBool(false),
            JStr("lolis"),
            JArr {
              Vector(1, 2, 3).map(JNum(_))
            },
          )
        },
        "lolis" → JObj {
          Map(
            "x" → JNum(12),
            "y" → JNum(28),
            "tridimentional" → JBool(false),
            "name" → JStr("Samantha")
          )
        },
      )
    }
  }
}
