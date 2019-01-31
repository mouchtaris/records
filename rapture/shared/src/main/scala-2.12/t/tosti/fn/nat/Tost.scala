package t.tosti.fn.nat

import t.fn.nat._
import t.fn.pf.Def

object Tost {

  def expect_eq[A, B, R: Def[Add]#at[(A, B)]#t]: Unit = ()
  def expect_maker[A](implicit make: Def[t.fn.pfs.Make[A]]#at[Unit]#t[A]): Unit = ()

  expect_maker[_0]
  expect_maker[_1]
  expect_maker[_2]
  expect_maker[_3]
  expect_maker[_4]
  expect_maker[_5]
  expect_maker[_6]
  expect_maker[_7]
  expect_maker[_8]
  expect_maker[_9]

  expect_eq[_0, _0, _0]

  expect_eq[_0, _1, _1]
  expect_eq[_1, _0, _1]

  expect_eq[_0, _2, _2]
  expect_eq[_1, _1, _2]
  expect_eq[_2, _0, _2]

  expect_eq[_0, _3, _3]
  expect_eq[_1, _2, _3]
  expect_eq[_2, _1, _3]
  expect_eq[_3, _0, _3]

  expect_eq[_0, _4, _4]
  expect_eq[_1, _3, _4]
  expect_eq[_2, _2, _4]
  expect_eq[_3, _1, _4]
  expect_eq[_4, _0, _4]

  expect_eq[_0, _5, _5]
  expect_eq[_1, _4, _5]
  expect_eq[_2, _3, _5]
  expect_eq[_3, _2, _5]
  expect_eq[_4, _1, _5]
  expect_eq[_5, _0, _5]

  expect_eq[_0, _6, _6]
  expect_eq[_1, _5, _6]
  expect_eq[_2, _4, _6]
  expect_eq[_3, _3, _6]
  expect_eq[_4, _2, _6]
  expect_eq[_5, _1, _6]
  expect_eq[_6, _0, _6]

  expect_eq[_0, _7, _7]
  expect_eq[_1, _6, _7]
  expect_eq[_2, _5, _7]
  expect_eq[_3, _4, _7]
  expect_eq[_4, _3, _7]
  expect_eq[_5, _2, _7]
  expect_eq[_6, _1, _7]
  expect_eq[_7, _0, _7]

  expect_eq[_0, _8, _8]
  expect_eq[_1, _7, _8]
  expect_eq[_2, _6, _8]
  expect_eq[_3, _5, _8]
  expect_eq[_4, _4, _8]
  expect_eq[_5, _3, _8]
  expect_eq[_6, _2, _8]
  expect_eq[_7, _1, _8]
  expect_eq[_8, _0, _8]

  expect_eq[_0, _9, _9]
  expect_eq[_1, _8, _9]
  expect_eq[_2, _7, _9]
  expect_eq[_3, _6, _9]
  expect_eq[_4, _5, _9]
  expect_eq[_5, _4, _9]
  expect_eq[_6, _3, _9]
  expect_eq[_7, _2, _9]
  expect_eq[_8, _1, _9]
  expect_eq[_9, _0, _9]
}
