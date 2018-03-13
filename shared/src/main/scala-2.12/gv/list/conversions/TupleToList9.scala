package gv
package list
package conversions

trait TupleToList9 extends Any {

  final implicit def tuple9ToList[
    T1, T2, T3, T4, T5, T6, T7, T8, T9
  ]: ToList[
    Tuple9[T1, T2, T3, T4, T5, T6, T7, T8, T9],
    T1 :: T2 :: T3 :: T4 :: T5 :: T6 :: T7 :: T8 :: T9 :: Nil,
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
      Nil

}
