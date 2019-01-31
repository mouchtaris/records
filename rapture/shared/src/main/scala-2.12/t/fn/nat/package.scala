package t.fn

import pf.Pf

package object nat {

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

  final implicit class Make[N <: Nat](val nat: N) extends AnyVal with Pf[pfs.Make[N], Unit] {
    override type Out = N
    def apply(unit: Unit): N = nat
  }

  implicit val make0: Make[_0] = _0
  implicit val make1: Make[_1] = _1
  implicit val make2: Make[_2] = _2
  implicit val make3: Make[_3] = _3
  implicit val make4: Make[_4] = _4
  implicit val make5: Make[_5] = _5
  implicit val make6: Make[_6] = _6
  implicit val make7: Make[_7] = _7
  implicit val make8: Make[_8] = _8
  implicit val make9: Make[_9] = _9

}
