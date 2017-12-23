package gv
package list
package conversions

trait TupleToList13 extends Any {

  final implicit def tuple13ToList[
    T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13
  ]: ToList[
    Tuple13[T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13],
    T1 :: T2 :: T3 :: T4 :: T5 :: T6 :: T7 :: T8 :: T9 :: T10 :: T11 :: T12 :: T13 :: Nil,
  ] =
    tuple ⇒
      tuple._1 ::
      tuple._2 ::
      tuple._3 ::
      tuple._4 ::
      tuple._5 ::
      tuple._6 ::
      tuple._7 ::
      tuple._8 ::
      tuple._9 ::
      tuple._10 ::
      tuple._11 ::
      tuple._12 ::
      tuple._13 ::
      Nil

}
