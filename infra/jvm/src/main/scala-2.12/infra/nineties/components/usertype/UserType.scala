package infra
package nineties
package components
package usertype

sealed trait UserType extends Any

object UserType {

  case object Creative extends UserType
  case object Patron extends UserType

}
