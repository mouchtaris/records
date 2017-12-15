package gv
package list
package op
package contains

import
  fun._

trait EvidencePackage extends Any {

  final type Evidence[T, list <: List] = find.Evidence[TypeEquiv[T], list, T]

}
