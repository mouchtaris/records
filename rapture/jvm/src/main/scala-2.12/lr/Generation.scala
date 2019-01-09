package lr

import scala.annotation.tailrec
import scala.collection.immutable._
import lib.Debuggation
import lr.symbols.Terminal.ε

import scala.collection.GenTraversableOnce

object Generation {
  sealed trait Action
  final case class Shift() extends Action

  type SEquiv = State ⇒ State ⇒ Boolean
  type StateSet = TreeSet[State]
  type Actions = Transtyle[Action]
  type Gotos = Transtyle[Int]

  type TranstyleCore[T] = ListMap[Int, ListMap[symbols.Symbol, T]]
  final case class Transtyle[T](
    grammar: Grammar
  )(
    val core: TranstyleCore[T] = Transtyle.emptyCore[T](grammar)
  ) {
    val make = Transtyle[T](grammar) _
    val update: (((Int, symbols.Symbol), T)) ⇒ Transtyle[T] = {
      case ((stateId, symbol), action) ⇒
        val row0 = core.getOrElse(stateId, ListMap.empty)
        row0.get(symbol) match {
          case Some(obj) ⇒ throw new Exception(s"$obj / $action conflict for [$stateId, $symbol]")
          case _ ⇒ ()
        }
        val row1 = row0.updated(symbol, action)
        val core2 = core.updated(stateId, row1)
        make(core2)
    }
  }
  object Transtyle {
    def empty[T](grammar: Grammar) = Transtyle[T](grammar)(emptyCore[T](grammar))
    def emptyCore[T](grammar: Grammar): TranstyleCore[T] = ListMap.empty

    def numCols[T](actions: Transtyle[T]): Int = actions.grammar.symbolTable.size
    def nulRows[T](actions: Transtyle[T]): Int = actions.core.size
    final case class RenderData()
    def renderData[T](actions: Transtyle[T]) = {
      val symbolTable = actions.grammar.symbolTable
      val headers = symbolTable.keys.toVector
      val statesNum = actions.core.keys.foldLeft(0)(Math.max)
      val allValues = actions.core.values.flatMap(_.values)
      val allStrings = headers.map(_.toString) ++ allValues.map(_.toString)
      val colWidth = allStrings.map(_.length).foldLeft(3)(Math.max)
      val stateNumWidth = "%d".format(statesNum).length
      val colSep = " | "
      def col(s: Any): String = s"%${colWidth}s".format(s)
      def cols[T](t: TraversableOnce[T]): String = t.map(col).mkString(colSep)
      val header = {
        val statesHeader = " " * stateNumWidth
        s"$statesHeader || ${cols(headers)}"
      }
      def row(i: Int): String = {
        val rowId = s"%0${stateNumWidth}d".format(i)
        val row = actions.core.getOrElse(i, ListMap.empty)
        def item(s: symbols.Symbol) = row.get(s).map(_.toString).getOrElse("")
        val items = cols(headers.map(item))
        s"$rowId || $items"
      }
      val lines = Vector(header) ++ (0 to statesNum).map(row)
      lines mkString "\n"
    }
  }

  implicit val symbolOrdering: Ordering[symbols.Symbol] = Ordering.by(_.toString)
  implicit val expansionOrdering: Ordering[Expansion] = Ordering.by(_.value.toIterable)
  implicit val productionOrdering: Ordering[Production] = Ordering.by(p ⇒ (p.symbol, p.expansion))
  implicit val itemOrdering: Ordering[Item] = Ordering.by(i ⇒ (i.cursorIndex, i.production))
  implicit val stateOrdering: Ordering[State] = Ordering.by(_.items.toIterable)

  val sequiv: SEquiv = (Ordering[State].equiv _).curried

  def addAction[T](actions: Transtyle[T], n: Int, s: symbols.Symbol, a: T): Transtyle[T] =
    actions.update(((n, s), a))


  //
  // Generation state, while generating
  //
  case class GState(
    actions: Actions,
    gotos: Gotos,
    states: StateSet,
    unprocessedStates: Vector[State],
    nextId: Int,
    grammar: Grammar,
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
      def fup[T](actions: Transtyle[T]): String = Transtyle.renderData(actions)
      val actionsHeader = fup(actions)
      val gotosHeader = fup(gotos)
      s"$sstates\n$todo\n$actionsHeader\n$gotosHeader"
    }
  }

  object GState {
    def apply(grammar: Grammar, state0: State): GState =
      GState(
        actions = Transtyle.empty(grammar),
        gotos = Transtyle.empty(grammar),
        states = TreeSet.empty,
        unprocessedStates = Vector(state0),
        nextId = 1,
        grammar = grammar,
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
  // Create the first(A) set of the given symbol.
  //
  // first(A) is the set of terminal symbols that could begin
  // all A-productions.
  //
  def first(
    sym: symbols.Symbol,
    firsts: Map[symbols.Symbol, ListSet[symbols.Symbol]] = Map.empty
  ): ListSet[symbols.Symbol] =
    sym match {
      case _: symbols.Terminal ⇒
        ListSet(sym)
      case _: symbols.NonTerminal ⇒
        type SSet = ListSet[symbols.Symbol]
        type Mod = SSet ⇒ SSet
        object Mod {
          import symbols.Terminal.ε
          val noop: Mod = identity
          val addEmpty: Mod = _ + ε
          val hasEmpty: SSet ⇒ Boolean = _.contains(ε)
        }
        // All together
        type Firsts = Map[symbols.Symbol, SSet]
        type State = (Mod, Firsts)
        val state0: State = (Mod.noop, firsts)
        def getFirsts(sym: symbols.Symbol)(firsts: Firsts): (SSet, Firsts) =
          firsts.get(sym).map(fs ⇒ (fs, firsts)).getOrElse {
            val symFirsts = first(sym, firsts)
            val firsts1 = firsts.updated(sym, symFirsts)
            (symFirsts, firsts1)
          }
        val (mod, _): (Mod, Firsts) = grammar(sym).foldLeft(state0) {
          case ((accuMod, firsts), Production(symbol, Expansion(exp))) ⇒
            // if A := ε add ε to first(A)
            // if A =: Y... add first(Y) to first(A)
            // (if A non term and) if A := Y1Y2... add first(Yi) if ε in first(Yj) for 1 <= j < i
            val step0: Mod = if (exp.isEmpty) Mod.addEmpty else Mod.noop
            val state0 = (true, step0, firsts)
            val (_, mod, firsts1) = exp.foldLeft(state0) {
              // just skip the same symbol completely
              case (state, `sym`) ⇒ state
              case ((prevHasEmpty, accuMod, firsts), prodSym) ⇒
                val (symFirsts, firsts1): (SSet, Firsts) = getFirsts(prodSym)(firsts)
                val hasEmpty: Boolean = Mod.hasEmpty(symFirsts)
                val addFirsts: Mod = if (prevHasEmpty) _ ++ symFirsts else Mod.noop
                val nextMod: Mod = accuMod andThen addFirsts
                (hasEmpty, nextMod, firsts1)
            }
            (mod, firsts1)
        }
        mod(ListSet.empty)
  }

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
  def gstate0 = GState(grammar, state0)

  //
  // Generate the next generation state
  //
  def next(gstate: GState): GState = {
    import
      Generation.{ Transtyle, Actions, Shift, addAction }

    // only close i0 for the iteration of every looked-at symbol
    val i0 = gstate.i0

    def transit1(gstate: GState, sym: symbols.Symbol): GState = {
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
        gotos = addAction_(gstate.gotos, i1.i),
      )
    }

    def transit2(gstate: GState, sym: symbols.Symbol): GState = {
      gstate.i0.items
        .filter(_ isFinal)
      ???
    }

    def transit(gstate: GState, sym: symbols.Symbol): GState =
      transit1(gstate, sym)

    lookedAt(i0.dbI0)
      .dbLookedAt
      .foldLeft(gstate.processed)(transit)
  }
}
