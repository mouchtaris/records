package lr

import scala.collection.immutable._

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
    val itemExpansion: Item ⇒ ListSet[Item] =
      item ⇒
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
    val closure: ListSet[Item] ⇒ ListSet[Item] =
    set ⇒ {
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
  val advanceItem: Item ⇒ Item =
  item ⇒
    item copy (cursorIndex = item.cursorIndex + 1)

  val advanceState: State ⇒ State =
    state ⇒
      state copy (items = state.items map advanceItem)

  //
  // "close" a state is a fancy facade for closing the ItemSet of
  // a state and making a new state out of that.
  //
  // No ID changes.
  //
  val close: State ⇒ State =
  state ⇒
    state copy (items = closing.closure(state.items))

  //
  // Make a new State selecting only Items that have cursor
  // "looking at" a specific Symbol.
  //
  // No ID changes.
  //
  val selectLooking: symbols.Symbol ⇒ State ⇒ State =
  symbol ⇒ state ⇒
    state copy (items = state.items.filter(_.symbol == symbol))

  //
  // Finally, also rename states
  //
  // ID changes.
  //
  val rename: Int ⇒ State ⇒ State =
  id ⇒ state ⇒
    state copy (i = id)

  //
  // Select all symbols that are being "looked at" by cursors
  // in every Item in a State
  //
  val lookedAt: State ⇒ ListSet[symbols.Symbol] =
  State.lens.items andThen (_
    .map(_.cursorOpt)
    .collect { case Some(symbol) ⇒ symbol }
  )

  //
  // Make an initial state from the given symbol
  //
  val state0: symbols.NonTerminal ⇒ State =
    (grammar(_)) andThen (_.head) andThen Item.initial andThen (ListSet(_)) andThen (State(0, _)) andThen close

}
