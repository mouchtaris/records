package lr

import scala.collection.immutable._

object Grammars {

  import symbols.Terminal._
  import symbols.NonTerminal._

  val ExampleProductions: ListSet[Production] = Grammar.makeProductions(
    `<goal>` → Seq(S),

    S → Seq(E),
    E → Seq(E, x, E),
    E → Seq(z),
  )

  val Example: Grammar = ExampleProductions

  val Example2: Grammar = Grammar.makeProductions(
    `<goal>` → Seq(`<expr>`),

    `<expr>` → Seq(`<term>`, `+`, `<expr>`),
    `<expr>` → Seq(`<term>`),

    `<term>` → Seq(`<factor>`, `*`, `<term>`),
    `<term>` → Seq(`<factor>`),

    `<factor>` → Seq(`id`)
  )

}
