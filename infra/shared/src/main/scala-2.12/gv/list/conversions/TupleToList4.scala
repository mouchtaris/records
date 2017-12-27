package gv
package list
package conversions

trait TupleToList4 extends Any {

  final implicit def tuple4ToList[
    T1, T2, T3, T4
  ]: ToList[
    Tuple4[T1, T2, T3, T4],
    T1 :: T2 :: T3 :: T4 :: Nil,
  ] =
    tuple ⇒
      tuple._1 ::
      tuple._2 ::
      tuple._3 ::
      tuple._4 ::
      Nil

}
