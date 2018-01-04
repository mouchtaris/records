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

trait HttpRequestToMirrorDispatcher extends Any {

  protected[this] def mirrorHttpRequestHandling: MirrorHttpRequestHandling

  protected[this] def mirrors: Set[Mirror]

  def apply(): Flow[HttpRequest, HttpResponse, NotUsed]

}

