package infra

import scala.language.{higherKinds, implicitConversions}
import apr._

object AntePaliRecordsLib {
  import tpf._

  object kog {
    import tests.{email, id, created_at, modified_at}
    import list._
    import types._
    import list_map._
    import list_find._
    import The._
    import list_last._
    import tdb.{ tdb, wtdb }


    trait FieldEvidence[vals <: List]
      extends Any
      with TPF

    object FieldEvidence {
      sealed trait bind[vals <: List, T <: Type]
        extends Any
      {
        final type FindGetter = vals ListFind types_tpf.IsType[T]

        final type Evidence = FindGetter

        final type Defined =
          (FieldEvidence[vals] Apply T) {
            type Out = Evidence
          }
      }

      final implicit class Impl[vals <: List, T <: Type](
        val self: bind[vals, T]#Evidence
      )
        extends AnyVal
        with (FieldEvidence[vals] Apply T)
      {
        type bound = bind[vals, T]
        type Out = bound#Evidence
        def apply(T: T): Out = self
      }

      implicit def defined[vals <: List, T <: Type](
        implicit
        evidence: bind[vals, T]#Evidence
      ): Impl[vals, T] =
        evidence
    }


    final implicit class Closure[e[_ <: List], vals <: List](val self: vals) extends AnyVal {
    }

    sealed trait record[_fields <: List] extends Any {
      final type fields = _fields
      final type e[vals <: List] = (fields ListMap FieldEvidence[vals])
      final def apply[vals <: List](vals: vals): Closure[e, vals] = vals
    }

    object record {
      final implicit class Impl[f <: List](val self: f) extends AnyVal with record[f]
      def apply[fields <: List](fields: fields): Impl[fields] = fields
    }

    final implicit val account = record(email :: id :: Nil)

    def fr[vals <: List: account.e](vals: vals) = {
      account(vals)
    }

    object vals {
      val acc =
        email("bob@sponge.com") ::
        id(12) ::
        Nil
    }

    def omg = {
      // fr(vals.acc)
      infra.apr.wat.omg
    }

  }

  object list_last {
    import list._

    trait ListLast[-L <: List] extends Any {
      type Out
      def apply(list: L): Out
    }
    object ListLast {
      type Last[L <: List, out] = ListLast[L] {
        type Out = out
      }
      implicit def lastInHead[h]: (h :: Nil) Last h =
        new ListLast[h :: Nil] {
          type Out = h
          def apply(list: h :: Nil) = list.head
        }
      implicit def lastInTail[t <: List](implicit ev: ListLast[t]): (_ :: t) Last ev.Out =
        new ListLast[_ :: t] {
          type Out = ev.Out
          def apply(list: _ :: t) = ev(list.tail)
        }
    }
    final implicit class ListLastDeco[self <: List](val self: self)
      extends AnyVal
    {
      def last(implicit ev: ListLast[self]): ev.Out = ev(self)
    }
  }
 
  object tests {

    import list._
    import types._
    import The._
    import list_map._
    import list_find._
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

    final def poo = (
    //   fortheaccount(vals),
    //   fortheaccount(true :: id(28) :: "Yes" :: modified_at(89) :: 12 :: created_at(2017) :: email("bob") :: Nil),
    //   fortheaccount(id(28) :: 12 :: modified_at(900) :: email("bob") :: true :: created_at(89) :: Nil),
      omg
    )

    def omg = {
      kog.omg
    }
  }

  object tpf_compose {

    trait >>>[f <: TPF, g <: TPF] extends TPF

    object >>> {
      type Defined[f <: TPF, g <: TPF, at, out] = Apply[f >>> g, at] { type Out = out }

      implicit def defined[f <: TPF, g <: TPF, x, y](
        implicit
        f: (f Apply x) { type Out <: y },
        g: (g Apply y),
      ): Defined[f, g, x, g.Out] =
        Apply(x ⇒ g(f(x)))
    }
  }

  object records_closure_ops {

    import list._
    import list_find._
    import types.{ Type }

    final implicit class RecordClosureOpsDecoration[vals <: List](val vals: vals) extends AnyVal {
      def get[T <: Type](implicit find: ListFind[vals, infra.apr.types_tpf.IsType[T]] {type Out <: T#t}): T#t = find(vals)

      def getType[T](implicit find: ListFind[vals, apr.tpf_lib.IsType[T]] {type Out <: T}): T = find(vals)
    }

  }

  object records {
    import records_typedefs._
    import list._
    import list_find._
    import types._

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
    import list._
    import list_find._
    import types.{ Type }

    trait typedefs extends Any {
      // Type of Evidence for Types
      final type Getter[vals <: List, T <: Type] = ListFind[vals, infra.apr.types_tpf.IsType[T]] {type Out <: T#t}

      final type D[vals <: List, T <: Type] = Getter[vals, T]

      final type FindGetterTpf[vals <: List, T <: Type] = apr.tpf_lib.IsType[Getter[vals, T]]
    }

  }

}
