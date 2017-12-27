package gv
package list
package conversions

trait TupleToList2 extends Any {

  final implicit def tuple2ToList[
    T1, T2
  ]: ToList[
    Tuple2[T1, T2],
    T1 :: T2 :: Nil,
  ] =
    tuple ⇒
      tuple._1 ::
      tuple._2 ::
      Nil

}
