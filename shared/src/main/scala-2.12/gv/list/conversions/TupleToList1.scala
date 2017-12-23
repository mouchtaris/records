package gv
package list
package conversions

trait TupleToList1 extends Any {

  final implicit def tuple1ToList[
    T1
  ]: ToList[
    Tuple1[T1],
    T1 :: Nil,
  ] =
    tuple ⇒
      tuple._1 ::
      Nil

}
