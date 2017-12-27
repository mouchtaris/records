package gv
package list
package op
package get

import
  fun._

trait EvidencePackage extends Any {

  final type Evidence[T <: Type, list <: List] = find.Evidence[IsType[T], list, T#T]

}
