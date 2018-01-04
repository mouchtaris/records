package gv
package isi
package junix
package leon

import
  akka.{
    NotUsed,
  },
  akka.stream.scaladsl.{
    Flow,
  },
  akka.http.scaladsl.model.{
    HttpRequest,
    HttpResponse,
  }


trait MirrorHttpRequestHandling extends Any {

  def apply(mirror: Mirror): Flow[HttpRequest, HttpResponse, NotUsed]

}
