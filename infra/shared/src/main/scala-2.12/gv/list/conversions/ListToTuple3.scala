package gv
package list
package conversions

trait ListToTuple3 extends Any {

  final implicit def listToTuple3[
    T1, T2, T3
  ]: ToTuple[
    T1 :: T2 :: T3 :: Nil,
    Tuple3[T1, T2, T3],
  ] =
    t1 ⇒ {
      val t2 = t1.tail
      val t3 = t2.tail

      Tuple3(
        t1.head,
        t2.head,
        t3.head,
      )
    }

}
