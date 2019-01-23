package lr1.adt

final case class ParsingState(
  production: Production,
  cursorIndex: Int,
)
