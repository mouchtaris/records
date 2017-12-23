package gv
package list
package conversions

trait TupleToList3 extends Any {

  final implicit def tuple3ToList[
    T1, T2, T3
  ]: ToList[
    Tuple3[T1, T2, T3],
    T1 :: T2 :: T3 :: Nil,
  ] =
    tuple ⇒
      tuple._1 ::
      tuple._2 ::
      tuple._3 ::
      Nil

}
