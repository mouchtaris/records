package gv
package list

import
  string._

trait Package {
  type List = _ :: _

  trait ::[a, b <: List] {
    val head: a
    def tail: b
  }

  trait ListDeconstruct extends Any {
    def unapply[h, t <: List](list: h :: t): Option[(h, t)] =
      Some((list.head, list.tail))
  }

  object ::
    extends AnyRef
    with ListDeconstruct

  case class Cons[a, b <: List](
    head: a,
    tail: b
  ) extends (a :: b)

  sealed trait Nil extends (Nil :: Nil)
  val Nil: Nil = new Nil {
    val head = this
    def tail = this
  }

  trait ListBuilding[self <: List] extends Any {
    def self: self
    final def ::[h](h: h): h :: self = Cons(h, self)
  }

  implicit def nilToString: ToString[Nil] = new ToString[Nil] {
    def writeTo[w: Writable](w: w)(obj: Nil): Unit = lang.use {
      w ++= "Nil"
    }
  }
  implicit def listToString[h, t <: List: ToString]: ToString[h :: t] = new ToString[h :: t] {
    def writeTo[w: Writable](w: w)(obj: h :: t): Unit =
      obj.tail.writeTo(w ++= obj.head.toString ++= " :: ")
  }

}
