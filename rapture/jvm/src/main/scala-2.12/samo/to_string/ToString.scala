package samo.to_string

trait ToString[-T] extends Any {
  def apply(obj: T): String
}

object ToString {
  final implicit val intToString: ToString[Int] = _.toString
  final implicit val stringToString: ToString[String] = _.toString
}
