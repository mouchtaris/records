package lr

import scala.annotation.tailrec
import scala.collection.immutable._
import scala.util.{ Try, Success, Failure }
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

  trait SuperFunctionalFunctionDecorations extends Any {
    this: SuperFunctionalTemplate ⇒

    trait FunctionDecoration[A, B] {
      def self: A ⇒ B

      final def >>[C](other: B ⇒ C): A ⇒ C = self andThen other
      final def >>:(other: A): B = self(other)
      final def <<[C](other: C ⇒ A): C ⇒ B = other andThen self
      final def iff[C](other: C ⇒ A): C ⇒ B = self << other

      final type MapperEvidence[T, R] = (A ⇒ B) <:< ((T ⇒ R) ⇒ R)
      final class Mapper[T, R](
        implicit
        ev: MapperEvidence[T, R]
      ) {
        type Result[U] = (U ⇒ R) ⇒ R
        def apply[U](f: T ⇒ U): Result[U] =
          ur ⇒ {
            val tr: (T ⇒ R) ⇒ R = ev(self)
            tr(f >> ur)
          }
      }
      def map[T, R](implicit ev: MapperEvidence[T, R]): Mapper[T, R] =
        new Mapper[T, R]
    }

    private[this] final class Impl[A, B](override val self: A ⇒ B) extends FunctionDecoration[A, B]

    implicit def toDecoratedFunction[A, B](self: A ⇒ B): FunctionDecoration[A, B] =
      new Impl(self)
  }

  trait SuperFunctionalConditionDecorations extends Any {
    this: SuperFunctionalTemplate ⇒

    trait ConditionDecoration {
      def self: Condition
      final def &&(other: Condition): Condition = Condition.and(self)(other)
    }

    private[this] final class Impl(override val self: Condition) extends ConditionDecoration

    implicit def toConditionDecoration(self: Condition): ConditionDecoration = new Impl(self)
  }

  trait SuperFunctionalPredef extends Any {
    final def ?[T](implicit ev: T): T = ev
  }

  trait SuperFunctionalTesting {
    this: SuperFunctionalTemplate ⇒

    object Tests {
      type Result = Try[String]

      object Result {
        val `0`: Result = Success("<no tests>")
      }

      final case class ResultEv[R](toResult: Test[R] ⇒ Result)

      object ResultEv {
        implicit def fromBool: ResultEv[Boolean] = ResultEv {
          test ⇒
            if (test.run())
              Success(test.name)
            else
              Failure(new Exception(test.name))
        }
      }

      final case class Test[R](name: String, run: () ⇒ R)

      final case class ClosedTest[R](test: Test[R], resultEv: ResultEv[R]) {
        def run(): Result =
          resultEv.toResult(test)
      }

      object ClosedTest {
        implicit def fromTest[R: ResultEv](test: Test[R]): ClosedTest[R] =
          ClosedTest(test, implicitly)
      }

      // Helper mods
      val True: Condition = _ ⇒ true
      val False: Condition = _ ⇒ false
      val Spy: State.Mod = StateMod.addEmpty
      val SpiedWith: State.Mod ⇒ Stage ⇒ Boolean = fix ⇒ test ⇒ (fix >> test(Spy) >> (_.result.nonEmpty))(State.zero)
      val Spied: Stage ⇒ Boolean = SpiedWith(StateMod.`0`)

      type Tests = Vector[ClosedTest[_]]
      val BaseTests: Tests = Vector(
        Test("iff true always taken", () ⇒ Spied {
          iff(True)
        }),
        Test("iff false never taken", () ⇒ !Spied {
          iff(False)
        }),
        Test("conditional true always taken", () ⇒ Spied {
          spy ⇒ conditional(spy)(True)
        }),
        Test("conditional false never taken", () ⇒ !Spied {
          conditional(_)(False)
        }),
        Test("not iff true never taken", () ⇒ !Spied {
          iff(Condition.not(True))
        }),
        Test("not iff false always taken", () ⇒ Spied {
          iff(Condition.not(False))
        }),
        Test("conditional not true never taken", () ⇒ !Spied {
          conditional(_)(Condition.not(True))
        }),
        Test("conditional not false always taken", () ⇒ Spied {
          conditional(_)(Condition.not(False))
        }),
        Test("iff true and true taken", () ⇒ Spied {
          iff(True && True)
        }),
        Test("iff true and false not taken", () ⇒ !Spied {
          iff(True && False)
        }),
        Test("iff false and true not taken", () ⇒ !Spied {
          iff(False && True)
        }),
        Test("reading => true taken", () ⇒ Spied {
          iff(Condition.reading[Unit](_ ⇒ true)(()))
        }),
        Test("reading => false not taken", () ⇒ !Spied {
          iff(Condition.reading[Unit](_ ⇒ false)(()))
        }),
      )
      type TestsResults = Try[Vector[String]]

      object TestsResults {
        val zero: TestsResults = Success(Vector.empty)
      }

      val run: Tests ⇒ TestsResults = {
        val appendTo: Vector[String] ⇒ String ⇒ Vector[String] = v ⇒ s ⇒ v :+ s
        _
          .foldLeft(Tests.TestsResults.zero) { (resTry, test) ⇒
            resTry flatMap { appendTo >> test.run().map }
          }
      }

      val report: TestsResults ⇒ String =
        _
          .map(_.map(o ⇒ s"OK $o").mkString("\n"))
          .recover { case ex ⇒ s"FAILED FIRST: $ex" }
          .get

      val runReport: Tests ⇒ String =
        run >> report
    }

    def tests: Tests.Tests
    final def allTests: Tests.Tests =
      Tests.BaseTests ++ tests
  }

  trait FirstTesting {
    this: Any
      with First.type
    ⇒

    import Tests._

    private[this] object Fix {
      def sProduction(s: S): Production = Production(s, Seq.empty)

      val s1: S = symbols.Terminal.x
      val s2: S = symbols.Terminal.z
      assert(s1 != s2 && !s1.equals(s2))

      val isSelfCond: Condition = Condition.isSelf(s1)

      val isNotSelf: State.Mod =
        State.withProduction(sProduction(s2))
      val isSelf: State.Mod =
        State.withProduction(sProduction(s1))
    }

    final override def tests: Tests.Tests = {
      Vector(
        Test("isSelf", () ⇒ SpiedWith(Fix.isSelf) {
          iff(Fix.isSelfCond)
        }),
        Test("not isSelf", () ⇒ !SpiedWith(Fix.isSelf) {
          iff(Condition.not(Fix.isSelfCond))
        }),
        Test("isNotSelf", () ⇒ !SpiedWith(Fix.isNotSelf) {
          iff(Fix.isSelfCond)
        }),
        Test("not isNotSelf", () ⇒ SpiedWith(Fix.isNotSelf) {
          iff(Condition.not(Fix.isSelfCond))
        }),
      )
    }
  }

  trait SuperFunctionalTemplate extends AnyRef
    with SuperFunctionalFunctionDecorations
    with SuperFunctionalConditionDecorations
    with SuperFunctionalTesting
    with SuperFunctionalPredef
  {
    final type S = symbols.Symbol
    final type Set[T] = scala.collection.immutable.ListSet[T]
    final type Map[K, V] = scala.collection.immutable.ListMap[K, V]
    final val JavaConverters = scala.collection.JavaConverters

    //
    // Override
    //
    type State <: StateLike
    type StateCompanion <: StateCompanionLike
    type StateModCompanion <: StateModCompanionBase
    type ConditionCompanion <: ConditionBase
    val State: StateCompanion
    val StateMod: StateModCompanion
    val Condition: ConditionCompanion
    trait StateLike {
      val result: Set[S]
      val symbol: S
      val recursing: Set[S]
    }
    trait StateCompanionLike {
      final type Mod = State ⇒ State
      val withResult: Set.Mod ⇒ Mod
      val withSymbol: S ⇒ Mod
      val withRecursing: Set.Mod ⇒ Mod
      val zero: State
    }
    val masterMod: State.Mod // MasterMod

    final object Set {
      type Mod = Set[S] ⇒ Set[S]
      val zero: Set[S] = ListSet.empty
      val add: S ⇒ Mod = s ⇒ _ + s
      val addAll: GenTraversableOnce[S] ⇒ Mod = s ⇒ _ ++ s
    }

    final object S {
      val zero: S = symbols.Terminal.EOS
    }

    final type Stage = State.Mod ⇒ State.Mod

    trait StateModCompanionBase {
      final val `0`: State.Mod = identity
      final val updateResult: Set.Mod ⇒ State.Mod = State.withResult
      final val addSymbol: S ⇒ State.Mod = Set.add andThen updateResult
      final val addSymbols: GenTraversableOnce[S] ⇒ State.Mod = Set.addAll andThen updateResult
      final val addSelf: State.Mod = using(_.symbol)(addSymbol)
      final val addEmpty: State.Mod = addSymbol(ε)
      final val recurse: S ⇒ State.Mod =
        s ⇒
          iff(Condition.not(Condition.isRecursing(s))) {
            state ⇒ // IMPORTANT
              // Make the evaluation of the recursion body "lazy" by delaying
              // evaluation when the complete mod is called.
              // Otherwise, recursion is evaluated too eagerly, despite the
              // stop condition in "not(isRecursing(s))", because the body
              // is evaluated EITHERWAY (eagerly).
              val fix: State.Mod =
                State.withSymbol(s) >>
                  State.withRecursing(Set.addAll(state.recursing + s))
              val result: Set[S] = SuperFunctionalTemplate.this.apply(fix).result
              addSymbols(result)(state)
          }
    }

    final type Condition = State ⇒ Boolean
    final val iff: Condition ⇒ State.Mod ⇒ State.Mod =
      cond ⇒ mod ⇒ state ⇒
        if (cond(state)) mod(state) else state
    final val conditional: State.Mod ⇒ Condition ⇒ State.Mod =
      mod ⇒ cond ⇒
        iff(cond)(mod)

    trait ConditionBase {
      val not: Condition ⇒ Condition =
        cond ⇒ state ⇒
          !cond(state)
      val and: Condition ⇒ Condition ⇒ Condition =
        condA ⇒ condB ⇒ state ⇒
          condA(state) && condB(state)
      def reading[T](f: T ⇒ Boolean): T ⇒ Condition =
        obj ⇒ _ ⇒
          f(obj)

      val isRecursing: S ⇒ Condition =
        sym ⇒ state ⇒
          state.recursing.contains(sym)
    }

    // (t => m) => m
    // ((t => m) => m).map(t => u) => (u => m) => m
    final def usingOpt[T]: (State ⇒ Option[T]) ⇒ (T ⇒ State.Mod) ⇒ State.Mod =
      access ⇒ mod ⇒ state ⇒
        access(state)
          .map { obj ⇒ mod(obj) }
          .getOrElse(StateMod.`0`)
          .apply(state)

    final def using[T]: (State ⇒ T) ⇒ (T ⇒ State.Mod) ⇒ State.Mod =
      access ⇒
        usingOpt(s ⇒ Some(access(s)))

    final def foreachOpt[T]: (State ⇒ Option[Iterable[T]]) ⇒ (T ⇒ State.Mod) ⇒ State.Mod =
      iter ⇒ toMod ⇒ state ⇒ {
        val mod = iter(state)
          .map(_.foldLeft(StateMod.`0`) { (mod, t) ⇒ mod >> toMod(t) })
          .getOrElse(StateMod.`0`)
        mod(state)
      }

    final def foreach[T]: (State ⇒ Iterable[T]) ⇒ (T ⇒ State.Mod) ⇒ State.Mod =
      iter ⇒
        foreachOpt(s ⇒ Some(iter(s)))

    final def apply(fix: State.Mod): State =
      (fix >> masterMod)(State.zero)
  }

  //
  // Create the first(A) set of the given symbol.
  //
  // first(A) is the set of terminal symbols that could begin
  // all A-productions.
  //
  final object First extends AnyRef
    with SuperFunctionalTemplate
    with FirstTesting
  {
    final case class StateImpl(
      override val symbol: S,
      override val result: Set[S],
      override val recursing: Set[S],
      prodOpt: Option[Production] = None, // current production when iterating productions
      prevHasEmpty: Boolean = true, // previous production has ε when iterating productions
    ) extends StateLike


    object StateCompanionImpl extends StateCompanionLike {
      override val zero: State = StateImpl(
        result = Set.zero,
        symbol = S.zero,
        recursing = Set.zero,
      )
      override val withResult: Set.Mod ⇒ State.Mod =
        mod ⇒ state ⇒
          state.copy(result = mod(state.result))
      override val withSymbol: S ⇒ State.Mod =
        s ⇒
          _.copy(symbol = s)
      override val withRecursing: Set.Mod ⇒ State.Mod =
        mod ⇒ state ⇒
          state.copy(recursing = mod(state.recursing))
      val withProduction: Production ⇒ State.Mod =
        prod ⇒
          _.copy(prodOpt = Some(prod))
    }

    object StateModCompanion extends StateModCompanionBase {
    }

    object ConditionCompanion extends ConditionBase {
      val isTerminal: Condition = _.symbol.isInstanceOf[symbols.Terminal]
      val isExpEmpty: Condition = _.prodOpt exists (_.expansion.value.isEmpty)
      val isSelf: S ⇒ Condition =
        s ⇒ state ⇒
          state.prodOpt exists (_.symbol == s)
      val prevHasEmpty: Condition = _.prevHasEmpty
    }

    override type State = StateImpl
    override type StateCompanion = StateCompanionImpl.type
    override val State: StateCompanion = StateCompanionImpl
    override type StateModCompanion = StateModCompanion.type
    override val StateMod: StateModCompanion = StateModCompanion
    override type ConditionCompanion = ConditionCompanion.type
    override val Condition: ConditionCompanion = ConditionCompanion

    import StateMod.`0`

    val ifTerminal: State.Mod = {
      import Condition.isTerminal
      import StateMod.addSelf

      iff(isTerminal)(addSelf)
    }

    // if A := ε add ε to first(A)
    // if A =: Y... add first(Y) to first(A)
    // (if A non term and) if A := Y1Y2... add first(Yi) if ε in first(Yj) for 1 <= j < i
    val ifNotTerminal: State.Mod = {
      import
        Condition.{
          isTerminal,
          not,
          isExpEmpty,
          isSelf,
          prevHasEmpty,
        },
        StateMod.{
          addEmpty,
          recurse,
        }

      // if A := ε add ε to first(A)
      val ifExpEmpty: State.Mod =
        iff(isExpEmpty) {
          addEmpty
        }

      // if A := Y... add first(Y) to first(A)
      // (if A non term and) if A := Y1Y2... add first(Yi) if ε in first(Yj) for 1 <= j < i
      val ifExpNonEmpty: State.Mod =
        iff(not(isExpEmpty)) {
          foreachOpt(_.prodOpt map (_.expansion.value)) {
            expSym ⇒
              iff(not(isSelf(expSym)) && prevHasEmpty) {
                recurse(expSym)
              }
          }
        }

      iff(not(isTerminal)) {
        using(_.symbol) { symbol ⇒
          foreach(_ ⇒ grammar(symbol)) {
            State.withProduction(_) >>
              ifExpNonEmpty >>
              ifExpEmpty >>
              `0`
          }
        }
      }
    }

    override val masterMod: State.Mod = {
      `0` >>
        ifTerminal >>
        ifNotTerminal >>
        `0`
    }

    def apply(sym: S): Set[S] =
      masterMod(State.withSymbol(sym)(State.zero)).result
  }
  def first(s: symbols.Symbol): ListSet[symbols.Symbol] = First(s)
  def first2(
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
            val symFirsts = first2(sym, firsts)
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
  // Follow(A) -- terminals that can legally follow non-terminal A
  //
  object Follow {
    import symbols.{ Symbol ⇒ S }
    import scala.collection.immutable.{ ListSet ⇒ Set }

    final type Follow = Set[S]
    object Follow {
      val empty: Follow =
        Set.empty

      type Mod =
        Follow ⇒ Follow

      val add: S ⇒ Mod =
        s ⇒
          _ + s

      val addAll: Traversable[S] ⇒ Mod =
        s ⇒
          _ ++ s
    }

    final type Follows = Map[S, Follow]
    object Follows {
      val zero: Follows = Map.empty

      type Access = Follows ⇒ Follow

      val get: S ⇒ Access =
        s ⇒
          _.getOrElse(s, Follow.empty)


      type Mod = Follows ⇒ Follows
      object Mod {
        val noop: Mod = identity
        val zero: Mod = noop
      }

      val update: S ⇒ Follow.Mod ⇒ Mod =
        s ⇒ mod ⇒ self ⇒
          self.updated(s, mod(get(s)(self)))
    }

    final case class State(
      mod: Follows.Mod,
      prevExpSym: Option[S],
    )
    object State {
      val zero = State(
        mod = Follows.Mod.zero,
        prevExpSym = None,
      )
      //
      // Mod
      //
      type Mod = State ⇒ State
      val addMod: Follows.Mod ⇒ Mod =
        mod ⇒ state ⇒
          state.copy(mod = state.mod.andThen(mod))

      //
      // Pred
      //
      type Pred = State ⇒ Boolean
      val conditional: Pred ⇒ Mod ⇒ Mod =
        pred ⇒ mod ⇒ state ⇒
          if (pred(state)) mod(state) else state

      //
      // Accessors
      //
      def using[T]: (State ⇒ T) ⇒ (T ⇒ Mod) ⇒ Mod =
        access ⇒ mod ⇒ state ⇒
          mod(access(state))(state)
      def usingOpt[T]: (State ⇒ Option[T]) ⇒ (T ⇒ Mod) ⇒ Mod =
        access ⇒ mod ⇒ state ⇒
          access(state)
            .map(obj ⇒ mod(obj)(state))
            .getOrElse(state)

      //
      // Facades
      //
      object Pred {
        val hasPrevExpSym: Pred = _.prevExpSym.isDefined
      }
      object Mod {
        val addPlain: S ⇒ S ⇒ Mod =
          s ⇒ f ⇒ addMod(
            Follows.update(s)(Follow.add(f))
          )
        // if A := aBb then { First(b) - ε } in Follow(B)
        // if A := aBb and ε in First(b) then Follow(A) in Follow(B)
        val addFirstOfTo: S ⇒ S ⇒ Mod =
          of ⇒ to ⇒ {
            val firstOf: Set[S] = first(of)
            val hasEmpty: Boolean = firstOf.contains(ε)
            val firstOfPure: Set[S] = firstOf - ε
            val mod1: Follows.Mod = Follows.update(to)(Follow.addAll(firstOfPure))
            val mod2: Follows.Mod = if (hasEmpty) ??? else Follows.Mod.noop
            addMod( mod1 andThen mod2 )
          }
      }
    }

    def follows: S ⇒ Set[S] = {
      import symbols.Terminal.EOS
      import symbols.NonTerminal.`<goal>`
      import State.Mod.addFirstOfTo
      import State.Mod.addPlain
      import State.usingOpt

      val smod0 = addPlain(`<goal>`)(EOS)

      grammar.P.foldLeft(smod0) {
        case (smod, Production(prodSym, Expansion(prodExp))) ⇒
          prodExp.foldLeft(smod) {
            case (smod, prodSym) ⇒
              val smod1 = usingOpt(_.prevExpSym)(addFirstOfTo(prodSym))
              smod andThen smod1
          }
      }
      ???
    }

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
