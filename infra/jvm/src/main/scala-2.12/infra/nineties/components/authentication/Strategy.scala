package infra
package nineties
package components
package authentication

import
  scala.concurrent.{
    Future,
  }

trait Strategy {

  type Credentials

  def authenticate(credentials: Credentials): Future[Boolean]

}
