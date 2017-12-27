package gv
package list
package conversions

trait ListToTuple4 extends Any {

  final implicit def listToTuple4[
    T1, T2, T3, T4
  ]: ToTuple[
    T1 :: T2 :: T3 :: T4 :: Nil,
    Tuple4[T1, T2, T3, T4],
  ] =
    t1 ⇒ {
      val t2 = t1.tail
      val t3 = t2.tail
      val t4 = t3.tail

      Tuple4(
        t1.head,
        t2.head,
        t3.head,
        t4.head,
      )
    }

}
