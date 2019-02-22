package yc
package ph
package adt
package component

final class ProjectId(val value: String) extends AnyVal

object ProjectId {

  def apply(value: String) = new ProjectId(value)

}
