package gv
package list
package conversions

trait TupleToList8 extends Any {

  final implicit def tuple8ToList[
    T1, T2, T3, T4, T5, T6, T7, T8
  ]: ToList[
    Tuple8[T1, T2, T3, T4, T5, T6, T7, T8],
    T1 :: T2 :: T3 :: T4 :: T5 :: T6 :: T7 :: T8 :: Nil,
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
      Nil

}
