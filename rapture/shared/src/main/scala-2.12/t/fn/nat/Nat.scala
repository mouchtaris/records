package t.fn.nat

trait Nat extends Any

object Nat {

  type _0 = Zero
  type _1 = Succ[_0]
  type _2 = Succ[_1]
  type _3 = Succ[_2]
  type _4 = Succ[_3]
  type _5 = Succ[_4]
  type _6 = Succ[_5]
  type _7 = Succ[_6]
  type _8 = Succ[_7]
  type _9 = Succ[_8]

  implicit case object Zero extends Zero
  implicit val _0: _0 = Zero
  implicit val _1: _1 = Succ(_0)
  implicit val _2: _2 = Succ(_1)
  implicit val _3: _3 = Succ(_2)
  implicit val _4: _4 = Succ(_3)
  implicit val _5: _5 = Succ(_4)
  implicit val _6: _6 = Succ(_5)
  implicit val _7: _7 = Succ(_6)
  implicit val _8: _8 = Succ(_7)
  implicit val _9: _9 = Succ(_8)

}
