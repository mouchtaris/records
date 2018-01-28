package infra
package nineties

package object mdoel {

  type Future[+T] = scala.concurrent.Future[T]

  final class Email(val value: String) extends AnyVal
  final class Username(val value: String) extends AnyVal
  final class Bio(val value: String) extends AnyVal

  sealed trait UserType extends Any
  object UserType {
    case object Creative extends UserType
    case object Patron extends UserType
  }

  trait Authentication extends Any {
    type Credentials

    def authenticate(credentials: Credentials): Future[Boolean]
  }

  trait User extends Any {
    def account: Account
    def username: Username
    def bio: Bio
    def userType: UserType
  }

}
