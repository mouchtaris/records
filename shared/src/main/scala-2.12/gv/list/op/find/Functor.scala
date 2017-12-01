package gv
package list
package op
package find

trait Functor extends Any {

  final def apply[pf, list <: List, out: resultOf[pf, list]#λ](pf: pf)(list: list): out = list.find(pf)

}
