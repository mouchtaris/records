package infra
package apr

import scala.reflect.runtime.universe.{ TypeTag }
import The._
import apr.list.{ List, ::, Nil }
import tdb._
import types.{ Type, t }

object wat {
import wats._

  trait *[-pf, -x] extends Any {
    type result
    def apply(x: x): result
  }

  trait at[-pf, -x, r] extends Any with (pf * x) {
    final type result = r
  }


  trait evidence[e[_]] extends Any; object evidence {
    implicit def defined[e[_], x](
      implicit
      d: DummyImplicit,
      e: e[x],
    ): at[evidence[e], x, e[x]] =
      new at[evidence[e], x, e[x]] {
        def apply(x: x): e[x] = e
        override def toString: String = s"evidence[$e]"
      }
  }

  type FindGetter[vals, T] = list_select[find_type[T]] * vals
  type FieldEvidence[vals, T] = FindGetter[vals, T]

  trait field_evidence[vals] extends Any; object field_evidence {
    implicit def defined[vals, T](
      implicit
      d: DummyImplicit,
      ev: FieldEvidence[vals, T],
    ): at[field_evidence[vals], T, FieldEvidence[vals, T]] =
      _ => ev
  }
  abstract class record[fields](val fields: fields) {
    final type e[vals] = list_select[field_evidence[vals]] * fields
    final class Closure[vals, evs](val vals: vals, val evs: evs) {

    }
    final def apply[vals, evs](vals: vals)(
      implicit
      d: DummyImplicit,
      evs: at[ list_select[field_evidence[vals]], fields, evs],
    ): Closure[vals, evs] = new Closure(vals, evs(fields))
  }


  case object email extends t.String
  case object id extends t.Int
  case object account extends record(email :: id :: Nil)
  type email = email.type
  type id = id.type
  type account = account.type

  trait find_type[T] extends Any; object find_type {
    final implicit class Impl[T](val self: Unit) extends AnyVal
    def apply[T]: Impl[T] = ()
    def apply[T](T: T): Impl[T] = apply[T]
    implicit def `find_type * T`[T <: Type](implicit T: T): at[find_type[T], T.t, T.t] =
      new at[find_type[T], T.t, T.t] {
        def apply(t: T.t): T.t = t
        override def toString: String = s"find_type[$T]"
      }
  }

  val vals = email("bob@sponge.com") :: id(12) :: Nil
  type vals = vals.type
  def omg: Any = {
    (
      implicitly[account.e[vals]]
    )
  }

}
import wat._

trait pkg_list {

  sealed trait head; object head extends head {
    implicit def forList[h]: at[head, h :: List, h] =
      _.head
  }

  sealed trait tail; object tail extends tail {
    implicit def forList[t <: List]: at[tail, _ :: t, t] =
      _.tail
  }

}

trait pkg_list_select {
  this: 
    pkg_list
    =>

  trait list_select[pf] extends Any
  trait ListSelectLow extends Any {
    implicit def `list_select at Nil`[pred](
      implicit
      d: DummyImplicit
    ): at[list_select[pred], Nil, Nil] =
      new at[list_select[pred], Nil, Nil] {
        def apply(nil: Nil): Nil = nil
        override def toString: String = "list_select[Nil]"
      }
    implicit def `list_select at not_defined[pf]`[pred, list, t, tsel](
      implicit
      d: DummyImplicit,
      tail: at[tail, list, t],
      tsel: at[list_select[pred], t, tsel],
    ): at[list_select[pred], list, tsel] =
      new at[list_select[pred], list, tsel] {
        def apply(list: list): tsel = tsel(tail(list))
        override def toString: String = s"list_no_select :: $tsel"
      }
  }
  trait ListSelectHigh extends Any {
    implicit def `list_select at defined[pf]`[pred, list, h, t, hr, tsel <: List](
      implicit
      d: DummyImplicit,
      head: at[head, list, h],
      tail: at[tail, list, t],
      pred: at[pred, h, hr],
      tsel: at[list_select[pred], t, tsel],
    ): at[list_select[pred], list, hr :: tsel] =
      new at[list_select[pred], list, hr :: tsel] {
        def apply(list: list): hr :: tsel = pred(head(list)) :: tsel(tail(list))
        override def toString: String = s"list_select[$pred] :: $tsel"
      }
  }
  object list_select extends AnyRef
    with ListSelectLow
    with ListSelectHigh
  {
    import wats.{ list_select_impl => Impl }
    def apply[pf]: Impl[pf] = ()
    def apply[pf](pf: pf): Impl[pf] = ()
  }
}

trait pkg_pf_compose {
  trait >>>[a, b] extends Any; object >>> {
    implicit def defined_compose[f, g, x, y, z](
      implicit
      d: DummyImplicit,
      f: at[f, x, y],
      g: at[g, y, z],
    ): at[f >>> g, x, z] =
      x => g(f(x))
  }
}

trait pkg_eval {
  import wats.{ Evaluator }
  def eval[pf]: Evaluator[pf] = ()
  def eval[pf, x, r](pf: pf, x: x)(implicit ev: at[pf, x, r]): r = eval[pf](x)
}

object wats extends AnyRef
  with pkg_list
  with pkg_list_select
  with pkg_pf_compose
  with pkg_eval
{

  final implicit class list_select_impl[pf](val self: Unit) extends AnyVal with list_select[pf]
  final implicit class EvalDeco[s](val self: s) extends AnyVal {
    def eval[x](x: x)(implicit pf: s * x): pf.result =
      pf(x)
  }
  final implicit class Evaluator[pf](val self: Unit) extends AnyVal {
    def apply[x, r](x: x)(implicit pf: at[pf, x, r]): r = pf(x)
  }

}
