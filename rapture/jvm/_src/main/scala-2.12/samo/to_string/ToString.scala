package samo
package to_string

import pf._

trait ToString[-T] extends Any {
  def apply(obj: T): String
}

object ToString {
  final implicit val intToString: ToString[Int] = _.toString
  final implicit val stringToString: ToString[String] = _.toString

  implicit def stringPF[T: ToString]: PF[ToString.type, T, String] = _.string
}
