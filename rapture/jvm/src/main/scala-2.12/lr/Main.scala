package lr
import scala.collection.IterableView
import scala.collection.immutable._

object Main {

//    val initials: symbols ⇒ ListSet[Item] = (Productions.apply _).andThen(_.map(initial))
//    val symbolClosure: symbols ⇒ ListSet[Item] = initials andThen closure
//    val itemClosure: Item ⇒ ListSet[Item] = Item.lens.symbol andThen symbolClosure

//  object States {
//    val i0 = State(0, Items.symbolClosure(NonTerminal.`S'`))
//  }
//
//  trait Action extends Any
//  final case class GoTo(table: ListMap[(State, symbols), State])
//  final case class Parsing(
//    states: TreeSet[State],
//    unprocessedStates: ListSet[State],
//    goto: Vector[ListMap[symbols, State]],
//    action: Vector[ListMap[symbols, Action]],
//  )
//  object Parsing {
//    implicit val stateOrdering: Ordering[State] = Ordering.by(State.lens.items)
//    def apply() = Parsing(states = TreeSet.empty, unprocessedStates = ListSet(States.i0), goto = Vector.empty, action = Vector.empty)
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
//  }
//
  object Kitsch {
//    import States._
//    val i1 = States.i0.selectLookingAs(1, NonTerminal.S).advance.closed
//    val i2 = States.i0.selectLookingAs(2, NonTerminal.E).advance.closed
//    val i3 = States.i0.selectLookingAs(3, Terminal.z).advance.closed
//    val i4 = i2.selectLookingAs(4, Terminal.x).advance.closed
//    val i5 = i4.selectLookingAs(5, NonTerminal.E).advance.closed
//    val i6 = i5.advance.advance.advance
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
