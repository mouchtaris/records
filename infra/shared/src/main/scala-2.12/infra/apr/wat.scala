package infra
package apr

import scala.reflect.runtime.universe.{ TypeTag, typeOf }
import The._
import apr.list.{ List, ::, Nil }
import tdb._
import types.{ Type, t }

object wat {
import wats._

  type *[-pf, -x] = at[pf, x, _]
  trait at[-pf, -x, +r] extends Any {
    def apply(x: x): r
  }



  sealed trait email extends t.String
  sealed trait id extends t.Int
  final case object email extends email
  final case object id extends id

  val vals = email("bob@sponge.com") :: Nil
  type vals = vals.type
  type Vals = email.t :: Nil

  trait record extends Any {
    type e[vals]

    final type d[vals, T, t] =
      at[ list_select[find_type[T]], vals, t :: Nil ] ::
      Nil
  }

  final case object account extends record {

    type e[vals] =
      d[vals, email, email.t] ::
      Nil

    def get[T <: Type, vals](T: T)(vals: vals)(
      implicit
      d: DummyImplicit,
      evs: e[vals],
      getter: at[ list_select[is_type[d[vals, T, T.t]]], e[vals], d[vals, T, T.t] :: Nil ],
    ): T.t =
      ???
  }

  def doacc[vals: account.e](vals: vals): Any = {
    implicitly[at[
      is_type[account.d[vals, email, email.t]],
      account.d[vals, email, email.t],
      account.d[vals, email, email.t],
    ]]
    val lol = implicitly[at[
      list_select[is_type[account.d[vals,email, email.t]]],
      account.e[vals],
      account.d[vals, email, email.t] :: Nil 
    ]]
    account.get(email)(vals)(
      implicitly,
      implicitly,
      lol,
    )
  }

  def omg: Any = {
    doacc(vals)
  }


}
import wat._

trait pkg_list {

  sealed trait head; object head extends head {
    implicit def defined[h]: at[head, h :: List, h] =
      _.head
  }

  sealed trait tail; object tail extends tail {
    implicit def defined[t <: List]: at[tail, _ :: t, t] =
      _.tail
  }

}

trait pkg_find_type {
  this: Any
    =>

  trait find_type[T] extends Any; object find_type {
    implicit def `find_type * T`[T <: Type](implicit T: T): at[find_type[T], T.t, T.t] =
      new at[find_type[T], T.t, T.t] {
        def apply(t: T.t): T.t = t
        override def toString: String = s"find_type[$T]"
      }
  }

}

trait pkg_conforms {
  this: Any
    =>

  sealed trait <:<*[T] extends Any; object <:<* {
    implicit def defined[T, x](implicit ev: x <:< T): at[<:<*[T], x, T] =
      x => x
  }
}

trait pkg_list_find {
  this: Any
    with pkg_list_select
    with pkg_list
    =>

  sealed trait list_find[pred] extends Any; object list_find {
    implicit def defined[pred, x, sel, hsel](
      implicit
      d: DummyImplicit,
      sel: at[list_select[pred], x, sel],
      head: at[head, sel, hsel],
    ): at[list_find[pred], x, hsel] =
      new at[list_find[pred], x, hsel] {
        def apply(x: x): hsel = head(sel(x))
        override def toString: String = s"list_find($sel, $head)"
      }
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
    implicit def `list_select at not_defined[pf]`[pred, t <: List, tsel](
      implicit
      d: DummyImplicit,
      tsel: at[list_select[pred], t, tsel],
    ): at[list_select[pred], _ :: t, tsel] =
      new at[list_select[pred], _ :: t, tsel] {
        def apply(list: _ :: t): tsel = tsel(list.tail)
        override def toString: String = s"list_no_select :: $tsel"
      }
  }
  trait ListSelectHigh extends Any with ListSelectLow {
    implicit def `list_select at defined[pf]`[pred, h, t <: List, hr, tsel <: List](
      implicit
      d: DummyImplicit,
      pred: at[pred, h, hr],
      tsel: at[list_select[pred], t, tsel],
    ): at[list_select[pred], h :: t, hr :: tsel] =
      new at[list_select[pred], h :: t, hr :: tsel] {
        def apply(list: h :: t): hr :: tsel = pred(list.head) :: tsel(list.tail)
        override def toString: String = s"list_select[$pred] :: $tsel"
      }
  }
  object list_select extends AnyRef
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

trait pkg_weird_application {
  
  trait WeirdApplicationOps[self] extends Any {
    def self: self
    final def >::>[r](f: self => r): r = f(self)
  }

}

trait pkg_is_type {

  sealed trait is_type[T] extends Any; object is_type {

    implicit def `is_type[T] * T`[T]: at[ is_type[T], T, T] =
      T => T

  }

}

object wats extends AnyRef
  with pkg_list
  with pkg_list_select
  with pkg_pf_compose
  with pkg_eval
  with pkg_list_find
  with pkg_find_type
  with pkg_conforms
  with pkg_weird_application
  with pkg_is_type
{

  final implicit class list_select_impl[pf](val self: Unit) extends AnyVal with list_select[pf]

  final implicit class PfEvaluator[pf](val self: Unit) extends AnyVal {
    def apply[x, r](x: x)(implicit pf: at[pf, x, r]): r = pf(x)
  }
  final def pfeval[pf]: PfEvaluator[pf] = ()

  final implicit class EvalDeco[s](val self: s) extends AnyVal {
    def eval[x, r](x: x)(implicit pf: at[s, x, r]): r =
      pf(x)
  }
  final implicit class Evaluator[pf](val self: Unit) extends AnyVal {
    def apply[x, r](x: x)(implicit pf: at[pf, x, r]): r = pf(x)
  }
  final implicit class WeirdApplicationDeco[self](val self: self) extends AnyVal with WeirdApplicationOps[self]

}



object typemath {

  sealed trait _0 extends Any
  sealed trait +[x] extends Any
  sealed trait -[x] extends Any

  final type _1 = +[_0]
  final type _2 = +[_1]
  final type _3 = +[_2]
  final type _4 = +[_3]
  final type _5 = +[_4]
  final type _6 = +[_5]
  final type _7 = +[_6]
  final type _8 = +[_7]
  final type _9 = +[_8]

  trait eval[exp] extends Any { type out }
  final type ~~>[exp, o] = eval[exp] { type out = o }
  object eval extends AnyRef
    with `eval p0`
    with `eval p1`
    with `eval p2`
  {
    final implicit class ~~>[exp, o](val self: Unit) extends AnyVal with eval[exp] {
      type out = o
    }
    def apply[exp, out](): exp ~~> out = ()
  }

  trait `eval p0` extends Any {
    implicit def `eval default`[x]: x ~~> x = eval()
  }
  trait `eval p1` extends Any {
    implicit def `eval +-`[x]: +[-[x]] ~~> x = eval()
  }
  trait `eval p2` extends Any {
    implicit def `eval -+`[x]: -[+[x]] ~~> x = eval()
  }

}
