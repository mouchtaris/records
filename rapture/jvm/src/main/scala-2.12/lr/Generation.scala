package lr

import scala.annotation.tailrec
import scala.collection.immutable._

object Generation {
  sealed trait Action
  final case class Shift() extends Action

  type SEquiv = State ⇒ State ⇒ Boolean
  type StateSet = TreeSet[State]
  type Transtyle[T] = Vector[ListMap[symbols.Symbol, T]]
  type Actions = Transtyle[Action]
  type Gotos = Transtyle[State]

  implicit val symbolOrdering: Ordering[symbols.Symbol] = Ordering.by(_.toString)
  implicit val expansionOrdering: Ordering[Expansion] = Ordering.by(_.value.toIterable)
  implicit val productionOrdering: Ordering[Production] = Ordering.by(p ⇒ (p.symbol, p.expansion))
  implicit val itemOrdering: Ordering[Item] = Ordering.by(i ⇒ (i.cursorIndex, i.production))
  implicit val stateOrdering: Ordering[State] = Ordering.by(_.items.toIterable)

  val sequiv: SEquiv = (Ordering[State].equiv _).curried

  def addAction[T](actions: Transtyle[T], n: Int, s: symbols.Symbol, a: T): Transtyle[T] = {
    if (n > actions.size)
      throw new Exception("Assertion error: n can be at least 1 more than current n")
    if (n == actions.size)
      actions :+ ListMap(s → a)
    else
      actions.updated(n, actions(n).updated(s, a))
  }


  //
  // Generation state, while generating
  //
  case class GState(
    actions: Actions,
    gotos: Gotos,
    states: StateSet,
    unprocessedStates: Vector[State],
  ) {

    //
    // Reuse an equiv-state or the one given
    //
    def stateReuse(s: State): State =
      states.find(sequiv(s)).getOrElse(s)

    val i0: State = unprocessedStates.head
    val nextId: Int = i0.i + 1

    override def toString: String = {
      val states = unprocessedStates.mkString("\n")
      states
    }
  }

  object GState {
    def apply(state0: State): GState =
      GState(
        actions = Vector.empty,
        gotos = Vector.empty,
        states = TreeSet.empty,
        unprocessedStates = Vector(state0),
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
    state copy (items = state.items.filter(_.symbol == symbol))

  //
  // Finally, also rename states
  //
  // ID changes.
  //
  def rename(id: Int, state: State): State =
    state copy (i = id)

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
      gstate.{ i0, nextId, stateReuse },
      Generation.{ Transtyle, Shift, addAction }

    def transit(gstate: GState, trans: (symbols.Symbol, Int)): GState = {
      val (sym, id) = trans
      def addAction_[T](actions: Transtyle[T], obj: T): Transtyle[T] = addAction(actions, i0.i, sym, obj)

      val i1: State = stateReuse(rename(nextId + id, close(advanceState(selectLooking(sym, i0)))))
      val nextActions = sym match {
        case _: symbols.Terminal ⇒ addAction_(gstate.actions, Shift())
        case _ ⇒ gstate.actions
      }
      val nextGotos = addAction_(gstate.gotos, i1)
      val nextStates = gstate.states + i0
      val nextUnprocessedStates = gstate.unprocessedStates :+ i1
      GState(nextActions, nextGotos, nextStates, nextUnprocessedStates)
    }

    lookedAt(i0).zipWithIndex.foldLeft(gstate)(transit)
  }
}
