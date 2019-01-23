package lr1

import
  scala.collection.{
    Traversable,
    TraversableLike,
  },
  scala.collection.generic.{
    CanBuildFrom,
  },
  xtrm.list._

package object dsl {

  implicit def toSelf[T]: T To T =
    identity(_)

  implicit def toContainer[C[T] <: TraversableLike[T, C[T]], D[_], A, B](
    implicit
    a2b: A To B,
    cbf1: CanBuildFrom[C[A], B, C[B]],
    cbf2: CanBuildFrom[Nothing, B, D[B]],
  ): C[A] To D[B] =
    (as: C[A]) ⇒
      as.map(a2b.apply).to[D]

  implicit def nilToContainer[C[_], A](
    implicit
    cbf: CanBuildFrom[Nothing, Nothing, C[A]],
  ): Nil To C[A] =
    _ ⇒
      cbf().result()

  implicit def listToContainer[
    H,
    T <: List,
    C[CC] <: TraversableOnce[CC],
    A,
  ](
    implicit
    head: H To A,
    tail: T To C[A],
    cbf: CanBuildFrom[Nothing, A, C[A]],
  ): (H :: T) To C[A] = {
    list ⇒
      cbf().++=(tail(list.tail)).+=(head(list.head)).result()
  }

  implicit val terminalToSymbol: adt.Terminal To adt.Symbol =
    term ⇒
      adt.Symbol(Left(term))

  implicit val nonTerminalToSymbol: adt.NonTerminal To adt.Symbol =
    nonTerm ⇒
      adt.Symbol(Right(nonTerm))

  implicit def pairToProduction[A, B](
    implicit
    eva: A To adt.Symbol,
    evb: B To Set[Seq[adt.Symbol]],
  ): (A, B) To adt.Production = {
    case (a, b) ⇒
      adt.Production(
        symbol = eva(a),
        expansions = evb(b)
      )
  }

  implicit def prodSeqToGrammar[SP](
    implicit
    ev: SP To Seq[adt.Production]
  ): SP To adt.Grammar =
    sp ⇒
      adt.Grammar(
        productions = ev(sp)
      )

  final implicit class ClosedTo[B](val unit: Unit) extends AnyVal {
    def apply[A](a: A)(implicit ev: A To B): B =
      ev(a)
  }

  def to[B]: ClosedTo[B] =
    ()

  def prod[S, E](sym: S)(exps: E)(
    implicit
    toSym: S To adt.Symbol,
    toSeq: E To Set[Seq[adt.Symbol]],
  ): adt.Production =
    to { sym → exps }
}
