package gv
package list
package op
package map

trait Functor extends Any {

  final def apply[pf, list <: List, out <: List: resultOf[pf, list]#λ](pf: pf)(list: list): out =
    list map pf

}
