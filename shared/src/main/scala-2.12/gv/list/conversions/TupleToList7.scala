package gv
package list
package conversions

trait TupleToList7 extends Any {

  final implicit def tuple7ToList[
    T1, T2, T3, T4, T5, T6, T7
  ]: ToList[
    Tuple7[T1, T2, T3, T4, T5, T6, T7],
    T1 :: T2 :: T3 :: T4 :: T5 :: T6 :: T7 :: Nil,
  ] =
    tuple ⇒
      tuple._1 ::
      tuple._2 ::
      tuple._3 ::
      tuple._4 ::
      tuple._5 ::
      tuple._6 ::
      tuple._7 ::
      Nil

}
