package lr1

import dsl._
import xtrm.list._

object Main {

  class Example0 {

    object S {
      val goal = adt.NonTerminal("goal")
      val expr = adt.NonTerminal("expr")
      val term = adt.NonTerminal("term")
      val factor = adt.NonTerminal("factor")
      val + = adt.Terminal("+")
      val * = adt.Terminal("*")
      val id = adt.Terminal("id")
    }

    //val P: Set[adt.Production] = Set(
    val P =
      (S.goal → {
        (S.expr :: Nil) ::
          Nil
      }) ::
        (S.expr → {
          (S.term :: S.+ :: S.expr :: Nil) ::
            (S.term :: Nil) ::
            Nil
        }) ::
        (S.term → {
          (S.factor :: S.* :: S.term :: Nil) ::
            (S.factor :: Nil) ::
            Nil
        }) ::
        Nil
  }

  def main(args: Array[String]): Unit = {
    lr.Main.main(args)
    val ex0 = new Example0()
    import ex0._
    println( P )
    println( to[Set[adt.Production]](P) )
    println( new algebra.Start()(P) )
  }

}
