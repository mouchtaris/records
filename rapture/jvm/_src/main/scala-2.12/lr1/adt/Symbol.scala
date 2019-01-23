package lr1.adt

final case class Symbol(
  value: Either[Terminal, NonTerminal],
)
