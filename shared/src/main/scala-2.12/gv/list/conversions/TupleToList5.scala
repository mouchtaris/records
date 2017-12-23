package gv
package list
package conversions

trait TupleToList5 extends Any {

  final implicit def tuple5ToList[
    T1, T2, T3, T4, T5
  ]: ToList[
    Tuple5[T1, T2, T3, T4, T5],
    T1 :: T2 :: T3 :: T4 :: T5 :: Nil,
  ] =
    tuple ⇒
      tuple._1 ::
      tuple._2 ::
      tuple._3 ::
      tuple._4 ::
      tuple._5 ::
      Nil

}
