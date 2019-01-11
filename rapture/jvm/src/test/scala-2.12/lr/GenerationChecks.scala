package lr

import
  scala.collection.immutable._,
  org.scalacheck._,
  Prop._

object GenerationChecks
  extends Properties("lr.Generation")
{
  import
    Arbitrary.arbitrary,
    lr.Generation,
    lr.symbols
  final type S = symbols.Symbol
  final val N = symbols.Terminal
  final val Σ = symbols.NonTerminal
  final type Set[T] = scala.collection.immutable.Set[T]
  implicit def arbitraryFromGen[T](implicit gen: Gen[T]): Arbitrary[T] =
    Arbitrary(gen)

  implicit def arbitrarySymbol: Gen[symbols.Symbol] =
    Gen.oneOf(
      Σ.E,
      Σ.S,
      N.x,
      N.z
    )

  implicit def arbitraryProduction: Gen[Production] = for {
    s ← arbitrarySymbol
    exp ←
  }

  property("hello") = forAll { i: Int ⇒
    i == i
  }

}
