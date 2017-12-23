package gv
package list
package conversions

trait TupleToList6 extends Any {

  final implicit def tuple6ToList[
    T1, T2, T3, T4, T5, T6
  ]: ToList[
    Tuple6[T1, T2, T3, T4, T5, T6],
    T1 :: T2 :: T3 :: T4 :: T5 :: T6 :: Nil,
  ] =
    tuple ⇒
      tuple._1 ::
      tuple._2 ::
      tuple._3 ::
      tuple._4 ::
      tuple._5 ::
      tuple._6 ::
      Nil

}
