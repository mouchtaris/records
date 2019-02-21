package yc
package ph
package detail
package configuration

import java.net.URI

import ph.{configuration ⇒ outer}

object Fake {

  val gitlab: outer.GitLab = outer.GitLab(
    baseUrl = URI.create("http://somewhere.com")
  )

  val fake: outer.Configuration = outer.Configuration(
    gitlab = gitlab
  )

  def apply(): outer.Configuration = fake

}
