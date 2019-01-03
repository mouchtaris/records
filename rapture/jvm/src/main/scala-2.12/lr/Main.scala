package lr
import scala.collection.immutable._

object Main {

  trait Action extends Any
  final case class GoTo(table: ListMap[(State, symbols.Symbol), State])
  final case class Parsing(
    grammar: Grammar,
    states: TreeSet[State],
    unprocessedStates: Vector[State],
    goto: Vector[ListMap[symbols.Symbol, State]],
    action: Vector[ListMap[symbols.Symbol, Action]],
  )
  object Parsing {
    implicit val stateOrdering: Ordering[State] = Ordering.by(State.lens.items)
    def apply(grammar: Grammar): Parsing = apply(
      grammar = grammar,
      states = TreeSet.empty,
      unprocessedStates = Vector(Generation(grammar).state0(grammar.start)),
      goto = Vector.empty,
      action = Vector.empty
    )

    def generate(parsing: Parsing): Parsing = {
      val g = Generation(parsing grammar)
      val state = parsing.unprocessedStates.head
      val nextId = parsing.unprocessedStates.size
      val lookedAtSymbols: ListSet[symbols.Symbol] = g.lookedAt(state)
      val lookedAtProductions: ListSet[ListSet[Production]] = lookedAtSymbols map g.grammar.apply
      val lookedAtInitialItems: ListSet[ListSet[Item]] = lookedAtProductions map (_ map Item.initial)
      val lookedAtInitialStates: ListSet[State] =
        lookedAtInitialItems.zipWithIndex map {
          case (items, index) ⇒
            State(nextId + index, items)
        }
      val nextStates: ListSet[State] = lookedAtInitialStates map g.advanceState map g.close
      val newUnprocessedStates: Vector[State] = parsing.unprocessedStates.drop(1) ++ nextStates
      val newStates: TreeSet[State] = parsing.states + state
      ???
    }
//    def generate(parsing: Parsing): Parsing = {
//      parsing.unprocessedStates.foldLeft(parsing) { (parsing, state) ⇒
//        val curses: ListSet[symbols] = States.cursors(state)
//        ???
//      }
//    }
//    def generate(): Parsing = {
//      val zero = Parsing()
//      generate(zero)
//    }
//    val next: Parsing ⇒ Parsing =
//      parsing ⇒ {
//        val state = parsing.states.last
//        val curses: ListSet[symbols] = States.cursors(state)
//        val (_, newStates) = curses.foldLeft((parsing.nextId, Vector.empty[State])) {
//          case ((id, states), curs) ⇒
//            import States.StateDecoration
//            val newState: State = state.selectLookingAs(id, curs).advance.closed
//            (id + 1, states :+ newState)
//        }
//        ???
//      }
  }
//
  object Kitsch {
//    import States._
//    val i1 = States.i0.selectLookingAs(1, NonTerminal.S).advance.closed
//    val i2 = States.i0.selectLookingAs(2, NonTerminal.E).advance.closed
//    val i3 = States.i0.selectLookingAs(3, Terminal.z).advance.closed
//    val i4 = i2.selectLookingAs(4, Terminal.x).advance.closed
//    val i5 = i4.selectLookingAs(5, NonTerminal.E).advance.closed
//    val i6 = i5.advance.advance.advance
    val g = Generation(Grammars.Example)
    val i0 = g.state0(symbols.NonTerminal.S)

  }
  //
  // Sample grammar
  //
  //    S -> E
  //    E ->  E x E
  //        | z
  //
  def main(args: Array[String]): Unit = {
    println("Welcome to Parsing Meletalathron")
    println("== Grammar ==")
    println(Grammars.Example)
    println("== States ==")
    println(Kitsch.i0)
//    println(Productions.all.mkString("\n"))
//    println("========= ")
//    println(States.i0)
//    println(Kitsch.i1)
//    println(Kitsch.i2)
//    println(Kitsch.i3)
//    println(Kitsch.i4)
//    println(Kitsch.i5)
//    println(Kitsch.i6)
  }

}
