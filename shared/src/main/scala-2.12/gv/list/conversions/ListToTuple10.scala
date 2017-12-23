package gv
package list
package conversions

trait ListToTuple10 extends Any {

  final implicit def listToTuple10[
    T1, T2, T3, T4, T5, T6, T7, T8, T9, T10
  ]: ToTuple[
    T1 :: T2 :: T3 :: T4 :: T5 :: T6 :: T7 :: T8 :: T9 :: T10 :: Nil,
    Tuple10[T1, T2, T3, T4, T5, T6, T7, T8, T9, T10],
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
      val t10 = t9.tail

      Tuple10(
        t1.head,
        t2.head,
        t3.head,
        t4.head,
        t5.head,
        t6.head,
        t7.head,
        t8.head,
        t9.head,
        t10.head,
      )
    }

}
