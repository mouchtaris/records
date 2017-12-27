package gv
package list

trait ImportPackage {

  type  List                  = list.List
  type  Nil                   = list.Nil
  val   Nil                   = list.Nil

  val   ::                    = list.::
  type  ::[a, b <: list.List] = list.::[a, b]

}
