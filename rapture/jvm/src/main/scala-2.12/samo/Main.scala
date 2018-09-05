package samo

import xtrm.predef.Certainly
import list._
import list_case._
import to_string._
import list_to_string._
import pf._
import list_map._

object list_collect {
  trait Collect[F, L, R] extends Any {
    def apply(list: L): R
  }

  trait CollectLow extends Any {
    implicit def collectNil[
      F,
      L
    ]: Collect[F, L, L] =
      identity[L] _
  }

  trait CollectHigh extends Any  with CollectLow {
    implicit def collectList[
      F,
      L,
      H: List[L]#Head,
      T: List[L]#Tail,
      R,
    ](
      implicit
      tr: Collect[F, T, R],
      pf: PF[F, (R, H), R],
    ): Collect[F, L, R] =
      list ⇒
        pf((tr(list.tail), list.head))
  }

  final class BindF[F](val unit: Unit) extends AnyVal {
    def apply[L, R](list: L)(implicit collect: Collect[F, L, R]): R =
      collect(list)
  }

  trait CollectCompanion
    extends Any
    with CollectHigh
  {
    def apply[F] = new BindF[F](())
  }

  object Collect
    extends AnyRef
    with CollectCompanion
}

import list_collect._

object Main {
  type Inspect = Inspect.type
  object Inspect {
    implicit def pf[T]: PF[Inspect, T, String] =
      obj ⇒
        s"${obj.getClass}[$obj]"
  }

  def main(args: Array[String]): Unit = {
    import list._
    import to_string._
    import list_case._
    val a @ s :: i :: nil = "12" ::: 12 ::: Nil
    a.string
    Collect[Inspect](a)
      // Collect.collectList[Inspect, String ::: Int ::: Nil.type, String, Int ::: Nil.type, String]
    println((s: String).string)
    println((i: Int).string)
    println((nil: Nil.type).string)
    println(12.string)
    println("HJel")
  }
}
