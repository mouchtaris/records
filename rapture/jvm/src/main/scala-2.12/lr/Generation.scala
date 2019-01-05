package lr

import scala.annotation.tailrec
import scala.collection.immutable._
import lib.Debuggation

object Generation {
  sealed trait Action
  final case class Shift() extends Action

  type SEquiv = State ⇒ State ⇒ Boolean
  type StateSet = TreeSet[State]
  type Actions = Transtyle[Action]
  type Gotos = Transtyle[State]

  type Transtyle[T] = ListMap[Int, ListMap[symbols.Symbol, T]]
  object Transtyle {
    def empty[T]: Transtyle[T] = ListMap.empty

    def normalize[T](actions: Transtyle[T]): (Map[Int, symbols.Symbol], Vector[Option[T]]) = {
      val symbolMap: Map[symbols.Symbol, Int] =
        actions.values.flatMap(_.keys).zipWithIndex.toMap
      val symbolTable: Map[Int, symbols.Symbol] = symbolMap.map { case (s, i) ⇒ (i, s) }
      val symbolsOrdered: Vector[symbols.Symbol] = symbolTable.values.toVector
      val maxId: Int = actions.keys.foldLeft(0)(Math.max)
      val numSymbols: Int = symbolMap.size
      val b = Vector.newBuilder[Option[T]]
      b.sizeHint(maxId * numSymbols)
      (0 until maxId) foreach { i ⇒
        val entryMaybe: Option[ListMap[symbols.Symbol, T]] = actions.get(i)
        symbolsOrdered foreach { sym ⇒
          b += entryMaybe.flatMap(_.get(sym))
        }
      }
      (symbolTable, b.result())
    }
  }

  implicit val symbolOrdering: Ordering[symbols.Symbol] = Ordering.by(_.toString)
  implicit val expansionOrdering: Ordering[Expansion] = Ordering.by(_.value.toIterable)
  implicit val productionOrdering: Ordering[Production] = Ordering.by(p ⇒ (p.symbol, p.expansion))
  implicit val itemOrdering: Ordering[Item] = Ordering.by(i ⇒ (i.cursorIndex, i.production))
  implicit val stateOrdering: Ordering[State] = Ordering.by(_.items.toIterable)

  val sequiv: SEquiv = (Ordering[State].equiv _).curried

  def addAction[T](actions: Transtyle[T], n: Int, s: symbols.Symbol, a: T): Transtyle[T] =
    actions.updated(n, actions.getOrElse(n, ListMap.empty).updated(s, a))


  //
  // Generation state, while generating
  //
  case class GState(
    actions: Actions,
    gotos: Gotos,
    states: StateSet,
    unprocessedStates: Vector[State],
    nextId: Int,
  ) {

    //
    // Reuse an equiv-state or the one given
    //
    def stateReuse(s: State): (State, GState) =
      states.find(sequiv(s))
        .map((_, this))
        .getOrElse {
          val sNew = s.copy(i = nextId)
          val stateNew = copy(
            nextId = nextId + 1,
            unprocessedStates = unprocessedStates :+ sNew
          )
          (sNew, stateNew)
        }

    def i0: State = unprocessedStates.head
    def processed: GState = copy(
      states = states + i0,
      unprocessedStates = unprocessedStates.drop(1)
    )

    override def toString: String = {
      val todo = s"TODO: ${unprocessedStates.mkString(", ")}"
      val sstates = s"STATES:\n${states.mkString("\n")}"
      def fup[T](actions: Transtyle[T]): String = Transtyle.normalize(actions) match {
        case (st, vs) ⇒
          s"SymTable: ${ st.map { case (i, s) ⇒ s"[$i → $s]" }.mkString(".")
          }\nActions:\n${
            st.map("%15s".format(_)).mkString(" |__| ")
          }\n${
            st.map(_ ⇒ "-" * 15).mkString(" |__| ")
          }\n${
            if (vs.nonEmpty)
              vs.grouped(st.size)
                .zipWithIndex
                .map {
                  case (ss, idx) ⇒
                    ss.map("%15s".format(_)).mkString(" |%02d| ".format(idx))
                }
                .mkString("\n")
          }"
      }
      val actionsHeader = fup(gotos)
      s"$sstates\n$todo\n$actionsHeader"
    }
  }

  object GState {
    def apply(state0: State): GState =
      GState(
        actions = Transtyle.empty,
        gotos = Transtyle.empty,
        states = TreeSet.empty,
        unprocessedStates = Vector(state0),
        nextId = 1,
      )
  }
}

final case class Generation(
  grammar: Grammar
) {

  //
  // Closure of an ItemSet is the set augmented by all S-Productions
  // for every S that is looked-at by the cursor in every item in the
  // ItemSet.
  //
  object closing {

    //
    // The expansion of an Item is a Set that contains
    // - the item itself, and
    // - all S-Productions for every symbol S that is looked-at by the
    //    the cursor in the item
    //
    def itemExpansion(item: Item): ListSet[Item] =
      // Get (optionally) the symbol after the cursor -- say S
      item.cursorOpt // Option[Symbol] =>
        // Get all S-Productions from the grammar.
        // If the cursor is looking at the end (None), get an empty set
        .map(grammar.apply).getOrElse(ListSet.empty) // ListSet[Production] =>
        // Get initial items of productions
        .map(Item.initial) // ListSet[Item]
        // Preserve the initial item. Make first for clarity.
        .foldLeft(ListSet(item))(_ + _)


    //
    // The official closure of an ItemSet is the Set expanded by the
    // expansion of every item in the set.
    //
    // If the expansion of the set is the same as the original set,
    // no more expanding happens. Otherwise, we keep closing on the
    // expanding sets.
    //
    @tailrec
    def closure(set: ListSet[Item]): ListSet[Item] = {
      val set2 = set flatMap itemExpansion
      if (set == set2)
        set2
      else
        closure(set2)
    }

  }

  //
  // Advancements: advance the cursor one spot further on.
  //
  // This includes no extra checks, because no harm comes from
  // a cursor that is way too far to the right (other than sociopolitical).
  //
  def advanceItem(item: Item): Item =
    item copy (cursorIndex = item.cursorIndex + 1)

  def advanceState(state: State): State =
    state copy (items = state.items map advanceItem)

  //
  // "close" a state is a fancy facade for closing the ItemSet of
  // a state and making a new state out of that.
  //
  // No ID changes.
  //
  def close(state: State): State =
    state copy (items = closing.closure(state.items))

  //
  // Make a new State selecting only Items that have cursor
  // "looking at" a specific Symbol.
  //
  // No ID changes.
  //
  def selectLooking(symbol: symbols.Symbol, state: State): State =
    state copy (items = state.items.filter(_.cursorOpt.exists(_ == symbol)))

  //
  // Select all symbols that are being "looked at" by cursors
  // in every Item in a State
  //
  def lookedAt(state: State): ListSet[symbols.Symbol] =
    State.lens.items(state)
      .map(_.cursorOpt)
      .collect { case Some(symbol) ⇒ symbol }

  //
  // Make an initial state from the given symbol
  //
  def state0: State =
    (grammar(_))
      .andThen(_.head)
      .andThen(Item.initial)
      .andThen(ListSet(_))
      .andThen(State(0, _))
      .andThen(close)
      .apply(grammar.start)


  import Generation.GState
  //
  // Make an initial generation state
  //
  def gstate0 = GState(state0)

  //
  // Generate the next generation state
  //
  def next(gstate: GState): GState = {
    import
      Generation.{ Transtyle, Actions, Shift, addAction }

    // only close i0 for the iteration of every looked-at symbol
    val i0 = gstate.i0

    def transit(gstate: GState, sym: symbols.Symbol): GState = {
      def addAction_[T](actions: Transtyle[T], obj: T): Transtyle[T] = addAction(actions, i0.i, sym, obj)

      val (i1, gstate_): (State, GState) =
        gstate.stateReuse(
          close(
            advanceState(
              selectLooking(
                sym.dbLookingAt,
                i0
              ).dbSelectedLookingAt
            ).dbAdvancedStates
          ).dbClosed
        )
      i1.dbReused
      gstate_.nextId.dbNextId
      def nextActions: Actions = sym match {
        case _: symbols.Terminal ⇒ addAction_(gstate.actions, Shift())
        case _ ⇒ gstate.actions
      }
      gstate_.copy(
        actions = nextActions,
        gotos = addAction_(gstate.gotos, i1),
      )
    }

    lookedAt(i0.dbI0)
      .dbLookedAt
      .foldLeft(gstate.processed)(transit)
  }
}
