package infra
package nineties
package components
package account

import
  email.{
    Email,
  }

trait Account extends AnyRef {
  val email: Email
  val auth: authentication.Strategy
  val creds: auth.Credentials
}

