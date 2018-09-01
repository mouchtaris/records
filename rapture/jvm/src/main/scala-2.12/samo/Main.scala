package samo

import xtrm.predef.Certainly
import list._
import list_case._
import to_string._
import list_to_string._

trait PF[F, A, B] { def apply(a: A): B }
object PF {
  implicit class Applicator[F](val unit: Unit) extends AnyVal {
    def apply[A, R](a: A)(implicit pf: PF[F, A, R]): R = pf(a)
  }

  def apply[F]: Applicator[F] = ()
}


final class Applied[F, L](val value: L)
object Applied {
  implicit def head[
    F,
    L,
    H: List[L]#Head,
    R: Main.Defined[F]#At[H]#Result,
  ]: Head[Applied[F, L], R] =
    applied ⇒ {
      val list: L = applied.value
      val head: H = list.head
      val result: R = PF[F](head)
      result
    }

  implicit def tail[
    F,
    L,
    T: List[L]#Tail,
  ]: Tail[Applied[F, L], Applied[F, T]] =
    applied ⇒
      new Applied[F, T](applied.value.tail)
}

object Main {

  type Defined[F] = {
    type At[A] = {
      type Result[B] = PF[F, A, B]
    }
  }
  type StringPF = StringPF.type
  object StringPF {
    implicit def stringPF[T: ToString]: PF[StringPF, T, String] = _.string
  }

  type Inspect = Inspect.type
  object Inspect {
    implicit def pf[T]: PF[Inspect, T, String] =
      obj ⇒
        s"${obj.getClass}[$obj]"
  }

  final implicit class Apply[F](val unit: Unit) extends AnyVal
  object Apply {
    implicit def pf[F, L]: PF[Apply[F], L, Applied[F, L]] =
        list ⇒
          new Applied[F, L](list)
  }

  def main(args: Array[String]): Unit = {
    import list._
    import to_string._
    import list_case._
    val a @ s :: i :: nil = "12" ::: 12 ::: Nil
    a.string
    println((s: String).string)
    println((i: Int).string)
    println((nil: Nil.type).string)
    println(12.string)
    println("HJel")
  }
}
