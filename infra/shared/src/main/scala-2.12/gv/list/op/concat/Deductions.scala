package gv
package list
package op
package concat

trait Deductions extends Any {

  final implicit def nilCat[l2 <: List]: Evidence[Nil, l2, l2] =
    (nil, l2) ⇒ l2

  final implicit def listCat[h, t <: List, l2 <: List, tl2 <: List](
    implicit
    tcat: Evidence[t, l2, tl2]
  ): Evidence[h :: t, l2, h :: tl2] =
    (l1, l2) ⇒ l1.head :: tcat(l1.tail, l2)

}
