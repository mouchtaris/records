package gv
package list
package conversions

trait ListToTuple2 extends Any {

  final implicit def listToTuple2[
    T1, T2
  ]: ToTuple[
    T1 :: T2 :: Nil,
    Tuple2[T1, T2],
  ] =
    t1 ⇒ {
      val t2 = t1.tail

      Tuple2(
        t1.head,
        t2.head,
      )
    }

}
