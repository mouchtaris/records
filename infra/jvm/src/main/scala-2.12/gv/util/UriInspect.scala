package gv
package util

import
  java.net.{
    URI ⇒ jUri,
  },

  akka.http.scaladsl.model.{
    Uri ⇒ AkkaUri,
  },

  string._

object UriInspect {

  def jUriFields(uri: jUri): Stream[(String, Any)] =
    ("scheme" → uri.getScheme) #::
    ("authority" → uri.getAuthority) #::
    ("fragment" → uri.getFragment) #::
    ("user info" → uri.getUserInfo) #::
    ("host" → uri.getHost) #::
    ("port" → uri.getPort) #::
    ("scheme specific port" → uri.getSchemeSpecificPart) #::
    ("path" → uri.getPath) #::
    ("query" → uri.getQuery) #::
    ("parse server authority" → uri.parseServerAuthority()) #::
    Stream.empty

  def akkaUriFields(uri: AkkaUri): Stream[(String, Any)] =
    ("scheme" → uri.scheme) #::
    ("authority" → uri.authority) #::
    ("fragment" → uri.fragment) #::
    ("effective port" → uri.effectivePort) #::
    ("path" → uri.path) #::
    ("query" → uri.query()) #::
    ("query string" → uri.queryString()) #::
    ("raw query string" → uri.rawQueryString) #::
    Stream.empty

  implicit val JavaUriInspect: Inspect[jUri] = jUriFields _
  implicit val AkkaUriInspect: Inspect[AkkaUri] = akkaUriFields _

}
