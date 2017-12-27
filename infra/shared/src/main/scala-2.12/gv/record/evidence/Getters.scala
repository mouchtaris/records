package gv
package record
package evidence

import
  list.op._,
  list._,
  fun._

sealed trait Getters[fields <: List] {

  final type λ[vals <: List] = fold.Evidence[
    FindGetterIn[vals],
    fields,
    Named[_],
    vals ⇒ Named[_]#T
  ]

}

