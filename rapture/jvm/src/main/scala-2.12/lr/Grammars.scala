package lr

import scala.collection.immutable._

object Grammars {

  import symbols.Terminal._
  import symbols.NonTerminal._

  val ExampleProductions: ListSet[Production] = Grammar.makeProductions(
    `S'` → Seq(S),

    S → Seq(E),
    E → Seq(E, x, E),
    E → Seq(z),
  )

  val Example: Grammar = ExampleProductions

}
