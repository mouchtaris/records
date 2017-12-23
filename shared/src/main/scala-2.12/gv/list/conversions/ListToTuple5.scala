package gv
package list
package conversions

trait ListToTuple5 extends Any {

  final implicit def listToTuple5[
    T1, T2, T3, T4, T5
  ]: ToTuple[
    T1 :: T2 :: T3 :: T4 :: T5 :: Nil,
    Tuple5[T1, T2, T3, T4, T5],
  ] =
    t1 ⇒ {
      val t2 = t1.tail
      val t3 = t2.tail
      val t4 = t3.tail
      val t5 = t4.tail

      Tuple5(
        t1.head,
        t2.head,
        t3.head,
        t4.head,
        t5.head,
      )
    }

}
