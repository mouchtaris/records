package lr

object symbols {

  sealed trait Symbol extends Any

  sealed trait Terminal extends Any with Symbol

  object Terminal {

    final case object ε extends Terminal

    final case object x extends Terminal

    final case object z extends Terminal

    final case object e extends Terminal

    final case object EOS extends Terminal

  }

  sealed trait NonTerminal extends Any with Symbol

  object NonTerminal {

    final case object `<goal>` extends NonTerminal

    final case object S extends NonTerminal

    final case object E extends NonTerminal

  }

}
