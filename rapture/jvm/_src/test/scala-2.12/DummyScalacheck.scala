import org.scalacheck._
import Prop._

object Lol extends Properties("Lol") {
  property("true") = forAll { l: Vector[Int] ⇒
    l.reverse.reverse == l
  }
}
