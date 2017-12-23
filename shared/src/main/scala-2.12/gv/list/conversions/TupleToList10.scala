package gv
package list
package conversions

trait TupleToList10 extends Any {

  final implicit def tuple10ToList[
    T1, T2, T3, T4, T5, T6, T7, T8, T9, T10
  ]: ToList[
    Tuple10[T1, T2, T3, T4, T5, T6, T7, T8, T9, T10],
    T1 :: T2 :: T3 :: T4 :: T5 :: T6 :: T7 :: T8 :: T9 :: T10 :: Nil,
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
      Nil

}
