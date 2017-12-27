package gv
package list
package conversions

trait ListToTuple21 extends Any {

  final implicit def listToTuple21[
    T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21
  ]: ToTuple[
    T1 :: T2 :: T3 :: T4 :: T5 :: T6 :: T7 :: T8 :: T9 :: T10 :: T11 :: T12 :: T13 :: T14 :: T15 :: T16 :: T17 :: T18 :: T19 :: T20 :: T21 :: Nil,
    Tuple21[T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21],
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
      val t11 = t10.tail
      val t12 = t11.tail
      val t13 = t12.tail
      val t14 = t13.tail
      val t15 = t14.tail
      val t16 = t15.tail
      val t17 = t16.tail
      val t18 = t17.tail
      val t19 = t18.tail
      val t20 = t19.tail
      val t21 = t20.tail

      Tuple21(
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
        t11.head,
        t12.head,
        t13.head,
        t14.head,
        t15.head,
        t16.head,
        t17.head,
        t18.head,
        t19.head,
        t20.head,
        t21.head,
      )
    }

}
