package gv
package list
package conversions

trait ListToTuple9 extends Any {

  final implicit def listToTuple9[
    T1, T2, T3, T4, T5, T6, T7, T8, T9
  ]: ToTuple[
    T1 :: T2 :: T3 :: T4 :: T5 :: T6 :: T7 :: T8 :: T9 :: Nil,
    Tuple9[T1, T2, T3, T4, T5, T6, T7, T8, T9],
  ] =
    t1 ⇒ {
      val t2 = t1.tail
      val t3 = t2.tail
      val t4 = t3.tail
      val t5 = t4.tail
      val t6 = t5.tail
      val t7 = t6.tail
      val t8 = t7.tail
      val t9 = t8.tail

      Tuple9(
        t1.head,
        t2.head,
        t3.head,
        t4.head,
        t5.head,
        t6.head,
        t7.head,
        t8.head,
        t9.head,
      )
    }

}
