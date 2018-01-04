package gv
package isi
package junix
package leon

import
  akkadecos._

object DownloadManager {

  val uri2request: Uri ⇒ HttpRequest =
    RequestBuilding.Get(_)

  def apply[context]()(
    implicit
    _http: HttpExt,
    _materializer: Materializer,
  ): DownloadManager[context] =
    new DownloadManager[context] {
      val http = _http
      val materializer = _materializer
    }

  val downloadDispatcherName = "gv.leon.dispatchers.downloading"
  val downloadDispatcherAttribute = ActorAttributes dispatcher downloadDispatcherName
}

trait DownloadManager[Context] {
  import DownloadManager._
  final type Connection = connectionManager.Connection
  final type Input = (Uri, Context)
  final type Request = (HttpRequest, Context)
  final type Response = (Try[HttpResponse], Context)

  implicit val http: HttpExt
  implicit val materializer: Materializer

  sealed trait Result extends Any {
    def context: Context
  }
  object Results {
    final case class Downloading(context: Context, bytes: Source[ByteString, _]) extends Result
    final case class HttpError(context: Context, resp: HttpResponse) extends Result
    final case class OtherFailure(context: Context, ex: Throwable) extends Result
  }

  lazy val connectionManager = ConnectionManager[Context]
  import connectionManager.{
    connection,
  }

  lazy val prepareRequest: Input ⇒ Request = {
    case (uri, context) ⇒
      (
        uri2request(uri),
        context
      )
  }

  import _HttpResponse.{
    Success ⇒ HttpSuccess,
  }
  lazy val toResult: Response ⇒ Result = {
    case (Success(_HttpResponse(HttpSuccess(), entity)), context) ⇒
      Results.Downloading(context, entity.withSizeLimit(500 << 20).dataBytes)
    case (Success(resp), context) ⇒
      Results.HttpError(context, resp)
    case (Failure(ex), context) ⇒
      Results.OtherFailure(context, ex)
  }

  lazy val download: Flow[Input, Result, NotUsed] = {
    http.system.log debug " +++++++++++ DOWNLOAD STREAM REQUESTED"
    Flow[Input]
      .map(prepareRequest)
      .via(connection)
      .map(toResult)
  }
}
