package gv
package isi
package junix
package leon
package http

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
    StatusCodes,
    Uri,
  }

import
  gv.{ http ⇒ ghttp }

trait HttpRequestToMirrorDispatcherImpl
  extends Any
  with HttpRequestToMirrorDispatcher
{

  def reject(): HttpRequest ⇒ HttpResponse =
    _ ⇒
      HttpResponse(StatusCodes.NotFound)

  private[this] def toPrefix(p: String): Uri.Path =
    Uri.Path / p

  final def apply(): Flow[HttpRequest, HttpResponse, NotUsed] = mirrors match {

      case mirrors if mirrors.isEmpty ⇒
        Flow fromFunction reject()

      case mirrors ⇒
        val mirrorHandler = mirrorHttpRequestHandling.apply _
        val handlers: Seq[(Uri.Path, ghttp.Handler)] = mirrors
          .toStream
          .map { mirror ⇒ toPrefix(mirror.name) → mirrorHandler(mirror) }
        ghttp.Multiplexor(handlers: _*).handler

  }

}
