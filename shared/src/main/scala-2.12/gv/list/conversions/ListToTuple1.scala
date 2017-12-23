package gv
package list
package conversions

trait ListToTuple1 extends Any {

  final implicit def listToTuple1[
    T1
  ]: ToTuple[
    T1 :: Nil,
    Tuple1[T1],
  ] =
    t1 ⇒ {

      Tuple1(
        t1.head,
      )
    }

}
