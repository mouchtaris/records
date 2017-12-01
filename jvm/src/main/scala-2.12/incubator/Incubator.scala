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
  trait RecordEvidence {
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

    object Evidence {
      type Foldy[vals <: List] = Fold[FindGetterIn[vals], Fields, ScopedNamed[_], vals ⇒ ScopedNamed[_]#T]

      def apply[vals <: List](vals: vals)(implicit ev: Evidence[vals]): ev.type = ev
      implicit def implicitEvidence[vals <: List: Foldy]: Evidence[vals] = new Evidence
    }
    final class Evidence[vals <: List: Evidence.Foldy] {
      val foldy: Evidence.Foldy[vals] = implicitly

      def getEvidence[n <: ScopedNamed[_]](implicit n: n): Get[n, vals] =
        vals ⇒ foldy(n).apply(vals).asInstanceOf[n.T]

      def get[n <: ScopedNamed[_]](n: n)(vals: vals): n.T =
        foldy(n)(vals).asInstanceOf[n.T]
    }

    implicit def getEvidence[n <: ScopedNamed[_]: Implicitly, vals <: List](implicit ev: Evidence[vals]): Get[n, vals] = ev.getEvidence[n]
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


    println { vals.get(email) }
//    println { vals get createdAt }
  }

  def main(args: Array[String]): Unit = {
    import typebug.sinks.out
    doSomething(acc)
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

  trait StandardNamedTypes {
    trait ScopedNamed[T] extends Named[T]
    trait int extends ScopedNamed[Int]
    trait string extends ScopedNamed[String]
  }

}

object package_record {

  trait Record
    extends AnyRef
      with ImplicitlyAvailable
      with StandardNamedTypes
      with RecordEvidence
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
