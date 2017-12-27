package gv
package record
package evidence

import
  list._

trait TypeAliases extends Any {

  sealed trait `for`[fields <: List] {
    final type λ[vals <: List] = record.Evidence[fields, vals]
  }

}
