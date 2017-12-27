package gv
package list
package op.kvget

trait Deductions extends Any {

  final implicit def kvgetHead[
    a,
    ta <: List,
    b,
    tb <: List,
  ](
    implicit
    dummyImplicit: DummyImplicit,
  ): Evidence[a :: ta, b :: tb, a, b] =
    _.head

  final implicit def kvgetTail[
    a,
    ta <: List,
    b,
    tb <: List,
    k,
    v,
  ](
    implicit
    tget: Evidence[ta, tb, k, v]
  ): Evidence[a :: ta, b :: tb, k, v] =
    b ⇒ tget(b.tail)

}
