package incubator

import scala.reflect.{ClassTag, classTag},
  gv.{fun, lang, list, string, tag, types},
  list._,
  list.op._,
  string._,
  tag._,
  fun._,
  types._,
  lang._

import scala.reflect.runtime.universe.{Select ⇒ _, _},
  implicit_tricks._,
  package_named._,
  package_record._,
  package_weird._,
  package_evidence._

object package_evidence {
  trait RecordEvidence extends Any {
    this: Record ⇒

    trait FindGetterIn[vals <: List]
    implicit def definedFindGetterIn[
      vals <: List,
      n <: Named.of[T],
      T,
    ](
      implicit
      n: n,
      ev: find.Evidence[IsType[n], vals, T]
    ): Defined[FindGetterIn[vals], n, vals ⇒ T] =
      () ⇒ { case `n` ⇒ ev.apply }

    type Foldy[vals <: List] = Fold[FindGetterIn[vals], Fields, ScopedNamed[_], vals ⇒ ScopedNamed[_]#T]
    implicit def implicitEvidence[vals <: List: Foldy]: Evidence[vals] = new Evidence
    final class EvidenceCompanion {
      def apply[vals <: List](implicit ev: Evidence[vals]): ev.type = ev
      def apply[vals <: List: Evidence](vals: vals): Evidence[vals] = apply[vals]
    }
    final def Evidence: EvidenceCompanion = new EvidenceCompanion
    final class Evidence[vals <: List: Foldy] {
      val foldy: Foldy[vals] = implicitly

      def getEvidence[n <: ScopedNamed[_]](implicit n: n): get.Evidence[n, vals] =
        vals ⇒ foldy(n).apply(vals).asInstanceOf[n.T]
    }

    implicit def getEvidence[n <: ScopedNamed[_]: Implicitly, vals <: List](implicit ev: Evidence[vals]): get.Evidence[n, vals] = ev.getEvidence[n]
  }

  final implicit class RecordJoinDecoration[fields <: List](val self: Unit)
    extends AnyVal
    with Record
  {
    type Fields = fields
  }

  trait RecordJoin extends Any {
    this: Record ⇒

    def join[
      fields <: List
    ](r2: Record)(
      implicit
      fields: Concat[Fields, r2.Fields, fields]
    ): RecordJoinDecoration[fields] =
      ()

  }
}

object package_weird {
  trait Fold[pf, list <: List, in, out] {
    def apply(in: in): out
  }
  object Fold {
    implicit def fold2[
      pf,
      a <: in: ClassTag,
      b <: in: ClassTag,
      aout,
      bout,
      in,
      out,
    ](
      implicit
      pfa: Defined[pf, a, aout],
      pfb: Defined[pf, b, bout],
      eva: aout ⇒ out,
      evb: bout ⇒ out,
    ): Fold[pf, a :: b :: Nil, in, out] = {
      case a: a ⇒
        eva(pfa()(a))
      case b: b ⇒
        evb(pfb()(b))
    }

    implicit def foldList[
      pf,
      h <: in: ClassTag,
      t <: List,
      hout,
      in,
      out
    ](
      implicit
      pfh: Defined[pf, h, hout],
      tfold: Fold[pf, t, in, out],
      evh: hout ⇒ out,
    ): Fold[pf, h :: t, in, out] = {
      case h: h ⇒
        evh(pfh()(h))
      case other ⇒
        tfold(other)
    }
  }

  trait Concat[l1 <: List, l2 <: List, out <: List] extends Any {
    def apply(l1: l1, l2: l2): out
  }
  object Concat {
    implicit def nilCat[l2 <: List]: Concat[Nil, l2, l2] = (nil, l2) ⇒ l2
    implicit def listCat[h, t <: List, l2 <: List, tl2 <: List](
      implicit
      tcat: Concat[t, l2, tl2]
    ): Concat[h :: t, l2, h :: tl2] =
      (l1, l2) ⇒ l1.head :: tcat(l1.tail, l2)
  }
}

object Incubator {

  case object account extends Record {
    case object email extends string
    case object password extends string
    case object authentication extends string
    type Fields = email.type :: password.type :: authentication.type :: Nil
  }

  case object timestamped extends Record {
    case object createdAt extends int
    case object modifiedAt extends int
    type Fields = createdAt.type :: modifiedAt.type :: Nil
  }

  val accountWithTimestamp = account.join(timestamped)

  val acc = {
    import account._
    import timestamped._
    email("em@bob.com") :: password("pass") :: authentication("yesterday") ::
      createdAt(12) :: modifiedAt(24) :: Nil
  }
  type acc = account.email.T :: account.password.T :: account.authentication.T ::
    timestamped.createdAt.T :: timestamped.modifiedAt.T :: Nil

  def doSomething[
    vals <: List
      : account.Evidence
      : timestamped.Evidence
      ,
  ](vals: vals): Unit = {
    val accountEv = account Evidence vals
    val timestampedEv = timestamped Evidence vals
    import
      account._,
      timestamped._,
      vals._


//    println { vals.get(password) }
//    println { vals.get(email) }
//    println { vals.get(authentication) }
    println { vals.get(createdAt) }

  }

  def main(args: Array[String]): Unit = {
    import typebug.sinks.out
    doSomething(acc)
    typebug.inspect[accountWithTimestamp.Fields]
  }

}

object package_named {

  trait Named[base]
    extends AnyRef
      with Tagverse
      with ImplicitlyAvailable
      with fun.Type
  {
    final type T = Tagged[base]

    final type Base = base
  }

  object Named {

    type of[t] = Named[_] { type T = t }

  }

  trait StandardNamedTypes extends Any {
    trait ScopedNamed[T] extends Named[T]
    trait int extends ScopedNamed[Int]
    trait string extends ScopedNamed[String]
  }

}

object package_record {

  trait Record
    extends Any
      with StandardNamedTypes
      with RecordEvidence
      with RecordJoin
  {
    type Fields <: List
  }

}

object implicit_tricks {

  trait ImplicitConstruct[list <: List] extends Any {
    def apply(): list
  }
  trait ImplicitConstructDeductions {
    final implicit val implicitConstructNil: ImplicitConstruct[Nil] =
      () ⇒ Nil
    final implicit def implicitConstruct[h: Implicitly, t <: List: ImplicitConstruct]: ImplicitConstruct[h :: t] =
      () ⇒ implicitly[h] :: implicitly[ImplicitConstruct[t]].apply()
  }
  object ImplicitConstruct
    extends AnyRef
      with ImplicitConstructDeductions

  trait ImplicitlyAvailable {
    implicit def implicitlyAvailable: this.type = this
  }


  def the[t <: AnyRef](implicit t: t): t.type = t
}
