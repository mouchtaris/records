package lr

import
  scala.collection.immutable._,
  org.scalacheck.{
    Arbitrary ⇒ Arb,
    Gen,
    Prop,
    Properties
  },
  Prop._

object GenerationChecks
  extends Properties("lr.Generation")
{
  import
    lr.Generation,
    lr.symbols
  type S = symbols.Symbol
  type N = symbols.Terminal
  val N = symbols.Terminal
  type Σ = symbols.NonTerminal
  val Σ = symbols.NonTerminal
  type P = Production
  val P = Production
  def A[T](implicit arb: Arb[T]): Gen[T] =
    arb.arbitrary

  implicit def arbitraryTerminal: Arb[N] = Arb {
    Gen.oneOf(
      N.x,
      N.z,
    )
  }

  implicit def arbitraryNoNTerminal: Arb[Σ] = Arb {
    Gen.oneOf(
      Σ.E,
      Σ.S,
    )
  }

  implicit def arbitrarySymbol: Arb[S] = Arb {
    Gen.oneOf[S](
      arbitraryTerminal.arbitrary.map(identity[S]),
      arbitraryNoNTerminal.arbitrary.map(identity[S]),
    )
  }

  implicit def arbitraryExpansion: Arb[Expansion] = Arb {
    for {
      syms ← A[Seq[S]]
    }
      yield Expansion(syms.toVector)
  }

  implicit def arbitraryProduction: Arb[Production] = Arb {
    for {
      s ← A[S]
      exp ← A[Expansion]
    }
      yield Production(symbol = s, expansion = exp)
  }

  implicit def arbitraryGrammar: Arb[Grammar] = Arb {
    for {
      n ← A[Seq[N]]
      σ ← A[Seq[Σ]]
      p ← A[Seq[P]]
      start ← A[Σ]
    }
      yield Grammar(N = n.to[ListSet], Σ = σ.to[ListSet], P = p.to[ListSet], start = start)
  }

  implicit def arbitraryGeneration: Arb[Generation] = Arb {
    for {
      g ← A[Grammar]
    }
      yield Generation(g)
  }

  property("hello") = forAll { implicit g: Generation ⇒
    g == g
  }

}
