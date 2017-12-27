package gv
package list
package conversions

trait ListToTuple6 extends Any {

  final implicit def listToTuple6[
    T1, T2, T3, T4, T5, T6
  ]: ToTuple[
    T1 :: T2 :: T3 :: T4 :: T5 :: T6 :: Nil,
    Tuple6[T1, T2, T3, T4, T5, T6],
  ] =
    t1 ⇒ {
      val t2 = t1.tail
      val t3 = t2.tail
      val t4 = t3.tail
      val t5 = t4.tail
      val t6 = t5.tail

      Tuple6(
        t1.head,
        t2.head,
        t3.head,
        t4.head,
        t5.head,
        t6.head,
      )
    }

}
