package infra

import scala.language.higherKinds

object AntePaliRecordsLib {

  object tests {

    import list._
    import types._
    import The._
    import type_partial_function._
    import list_map._
    import list_find._
    import types_is_type_tpf._
    import list_implicit_construct._
    import types_to_tagged_tpf._
    import records._
    import records_closure_ops._

    case object email extends t.String

    case object id extends t.Int

    case object created_at extends t.Int

    case object modified_at extends t.Int

    final type email = email.type
    final type id = id.type
    final type created_at = created_at.type
    final type modified_at = modified_at.type

    case object timestamped extends record {
      type e[vals <: List] =
        D[vals, created_at] ::
          D[vals, modified_at] ::
          Nil
    }

    case object account extends record {
      type e[vals <: List] =
        D[vals, email] ::
          D[vals, id] ::
          Nil
    }

    final val vals = modified_at(3000) :: id(12) :: created_at(1821) :: email("gamidi") :: Nil
    final type vals = vals.type

    // def fortheaccount[vals <: List : account.e : timestamped.e](vals: vals) = {
    //   val acc = account(vals)
    //   val ts = timestamped(vals)
    //   s"""
    //      | === Welcome to MENSA Biosystems, ${acc get email} ===
    //      | * User ID: ${acc get id}
    //      |
    //      | You were created at ${ts get created_at} and you last
    //      | changed your details at ${ts get modified_at}.
    //      |
    //      | Have a nice day,
    //      | The curator system.
    //    """.stripMargin
    // }

    final val poo = (
    //   fortheaccount(vals),
    //   fortheaccount(true :: id(28) :: "Yes" :: modified_at(89) :: 12 :: created_at(2017) :: email("bob") :: Nil),
    //   fortheaccount(id(28) :: 12 :: modified_at(900) :: email("bob") :: true :: created_at(89) :: Nil),
      omg
    )

    def omg = {
      implicit case object a { def apply() = a :: Nil }; type a = a.type :: Nil
      implicit case object b { def apply() = b :: a :: a() :: Nil }; type b = b.type :: a.type :: a :: Nil
      val r1 =
        the[a] ::
          the[b] ::
          the[a :: b :: Nil] ::
          the [b :: a :: Nil] ::
          the[a :: b :: a :: b :: DummyImplicit :: b :: b :: b :: a :: DummyImplicit :: Nil] ::
          Nil
      the[ListFind[r1.type, is_type_tpf.IsTypeTpf[a :: b :: a :: List]]].apply(r1)
      import tpf_compose._

      type apply[tpf <: TPF, at] = DefinedTpf[tpf, at]
      type IsT[T] = is_type_tpf.IsTypeTpf[T]
      type lol = ListFind[r1.type, list_tpfs.Head >>> IsT[b]]

      val tinput = a() :: b() :: Nil 
      type f1 = list_tpfs.Head
      type f2 = list_tpfs.Tail  >>> f1
      type f3 = f2 >>> IsT[b]
      //the[f1 apply tinput.type].apply(tinput) ::
        //the[f2 apply tinput.type].apply(tinput) ::
        the[f3 apply tinput.type].apply(tinput) ::
        //the[(`F.list.head` >>> `F.list.head`) DefinedTpf tinput.type].apply(tinput) ::
        Nil
    }
  }

  object tpf_compose {
    import type_partial_function._

    trait >>>[f <: TPF, g <: TPF] extends TPF

    object >>> {
      implicit def definedCompose[f <: TPF, g <: TPF, x, y](
        implicit
        f: DefinedTpf[f, x] { type Out <: y },
        g: DefinedTpf[g, y]
      ): DefinedTpf[f >>> g, x] { type Out = g.Out } =
        DefinedTpf(x => g(f(x)))
    }
  }

  object list_tpfs {
    import list._
    import type_partial_function._
    trait Head extends TPF
    object Head {
      implicit def definedListHead[h]: DefinedTpf[Head, h :: List] { type Out = h } =
        DefinedTpf(_.head)
    }
    trait Tail extends TPF
    object Tail {
      implicit def definedListTail[t <: List]: DefinedTpf[Tail, _ :: t] { type Out = t } =
        DefinedTpf(_.tail)
    }
  }

  object records_closure_ops {

    import list._
    import types._
    import list_find._
    import types_is_type_tpf._

    final implicit class RecordClosureOpsDecoration[vals <: List](val vals: vals) extends AnyVal {
      def get[T <: Type](implicit find: ListFind[vals, IsTypeTpf[T]] {type Out <: T#t}): T#t = find(vals)

      def getType[T](implicit find: ListFind[vals, is_type_tpf.IsTypeTpf[T]] {type Out <: T}): T = find(vals)
    }

  }

  object records {
    import records_typedefs._

    import types._
    import list._
    import list_find._

    object record
      extends AnyRef
        with typedefs {
      final type GetEv[e[_ <: List] <: List, vals <: List, T <: Type] =
        ListFind[e[vals], FindGetterTpf[vals, T]] {type Out <: Getter[vals, T]}

      final implicit class Closed[e[_ <: List] <: List, vals <: List](val vals: vals) extends AnyVal {
        def get[T <: Type](implicit e: e[vals], ev: GetEv[e, vals, T]): T#t = ev(e).apply(vals)

        def get[T <: Type](T: T)(implicit e: e[vals], ev: GetEv[e, vals, T]): T#t = get[T]
      }

    }

    trait record
      extends Any
        with typedefs {
      type e[vals <: List] <: List

      def apply[vals <: List : e](vals: vals): record.Closed[e, vals] = vals
    }


  }

  object records_typedefs {
    import types._
    import list._
    import list_find._
    import types_is_type_tpf._

    trait typedefs extends Any {
      // Type of Evidence for Types
      final type Getter[vals <: List, T <: Type] = ListFind[vals, IsTypeTpf[T]] {type Out <: T#t}

      final type D[vals <: List, T <: Type] = Getter[vals, T]

      final type FindGetterTpf[vals <: List, T <: Type] = is_type_tpf.IsTypeTpf[Getter[vals, T]]
    }

  }

  object list_find {

    import list._
    import type_partial_function._

    trait ListFind[-L <: List, tpf <: TPF] {
      type Out
      implicit val ev: DefinedTpf[tpf, Out]

      def apply(list: L): Out
    }

    object ListFind {
      type Aux[-L <: List, tpf <: TPF, out] = ListFind[L, tpf] {type Out = out}

      implicit def findInHead[h, tpf <: TPF](implicit tpf: DefinedTpf[tpf, h]): Aux[h :: List, tpf, h] =
        new ListFind[h :: List, tpf] {
          type Out = h
          val ev: tpf.type = tpf

          def apply(list: h :: List): h = list.head
        }

      implicit def findInTail[t <: List, tpf <: TPF](implicit tev: ListFind[t, tpf]): Aux[_ :: t, tpf, tev.Out] =
        new ListFind[_ :: t, tpf] {
          type Out = tev.Out
          val ev: tev.ev.type = tev.ev

          def apply(list: _ :: t): Out = tev(list.tail)
        }

    }

  }

  object list_map {

    import list._
    import type_partial_function._

    trait ListMap[-L <: List, tpf <: TPF] {
      type Out <: List

      def apply(list: L): Out
    }

    object ListMap {
      type Aux[-L <: List, tpf <: TPF, out <: List] = ListMap[L, tpf] {type Out = out}
    }

    import ListMap.Aux

    implicit def nilMap[tpf <: TPF]: Aux[Nil, tpf, Nil] =
      new ListMap[Nil, tpf] {
        type Out = Nil

        def apply(list: Nil): Nil = list
      }

    implicit def listMap[h, t <: List, tpf <: TPF, tpfOut, evOut <: List](
      implicit
      tpf: DefinedTpf[tpf, h] {type Out = tpfOut},
      ev: ListMap[t, tpf] {type Out = evOut}
    ): Aux[h :: t, tpf, tpfOut :: evOut] =
      new ListMap[h :: t, tpf] {
        type Out = tpf.Out :: ev.Out

        def apply(list: h :: t): Out =
          tpf(list.head) :: ev(list.tail)
      }

  }

  object type_partial_function {

    trait TPF // Type Partial Function
      extends Any

    sealed abstract class DefinedTpf[tpf <: TPF, -at] {
      type Out

      def apply(at: at): Out
    }

    object DefinedTpf {
      def apply[tpf <: TPF, at, out](f: at ⇒ out) =
        new DefinedTpf[tpf, at] {
          type Out = out

          def apply(at: at): Out = f(at)
        }

      final def empty[tpf <: TPF, at, out] =
        new DefinedTpf[tpf, at] {
          type Out = out

          def apply(at: at): Out = ???
        }
    }

  }

  object types_is_type_tpf {

    import types._
    import type_partial_function._

    sealed trait IsTypeTpf[T <: Type] extends TPF

    final object IsTypeTpf {
      type Defined[T <: Type, at, out] = DefinedTpf[IsTypeTpf[T], at] {type Out = out}

      implicit def defined[T <: Type](implicit T: T): Defined[T, T.t, T.t] = DefinedTpf(t ⇒ t)
    }

  }

  object types_to_tagged_tpf {

    import types._
    import type_partial_function._

    sealed trait ToTaggedTpf extends TPF

    object ToTaggedTpf {
      final type Defined[at, out] = DefinedTpf[ToTaggedTpf, at] {type Out = out}

      final implicit def defined[T <: Type](implicit T: T): Defined[T, T.t] = DefinedTpf.empty
    }

  }

  object is_type_tpf {

    import type_partial_function._

    sealed trait IsTypeTpf[T] extends TPF

    object IsTypeTpf {
      final type Defined[T, at] = DefinedTpf[IsTypeTpf[T], at] {type Out = T}

      implicit def defined[T, at](implicit ev: at <:< T): Defined[T, at] = DefinedTpf(t ⇒ t)
    }

  }

  object types {

    trait Type {
      type Base

      sealed trait Tag extends Any

      final type Tagged = Base with Tag
      final type t = Tagged

      def apply(b: Base): Tagged = b.asInstanceOf[Tagged]

      def unapply(t: Tagged): Option[Base] = Some(t)

      implicit def implicitlyAvailable: this.type = this
    }

    trait TypeB[B] extends Type {
      final type Base = B
    }

    object t {

      trait String extends Type {
        type Base = scala.Predef.String
      } // TypeB[scala.Predef.String]
      trait Int extends Type {
        type Base = scala.Int
      } // TypeB[scala.Int]
      trait Boolean extends TypeB[scala.Boolean]

    }

  }

  object list_implicit_construct {

    import list._

    implicit def implicitlyNil: Nil = Nil

    implicit def implicitlyList[h, t <: List](
      implicit
      h: h,
      t: t
    ): h :: t =
      h :: t
  }

  object list {

    trait List

    final case class ::[+a, +b <: List](
      head: a,
      tail: b
    )
      extends AnyRef
        with List
    {
      override lazy val toString: String =
        s"$head :: $tail"
    }

    final case object Nil extends List

    final type Nil = Nil.type

    final implicit class ListOps[+s <: List](val self: s) extends AnyVal {
      def ::[h](head: h): h :: s = list.::(head, self)
    }

  }

  object The {
    def the[t <: AnyRef](implicit t: t): t.type = t
  }


}
