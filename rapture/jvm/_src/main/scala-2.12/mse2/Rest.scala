package mse2

import scala.util.Try
import scala.util.Success
import scala.util.Failure
import scala.concurrent.Future
import scala.collection.immutable._

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.HttpHeader
import akka.http.scaladsl.model.HttpRequest
import akka.http.scaladsl.model.HttpResponse
import akka.http.scaladsl.model.HttpMethod
import akka.http.scaladsl.model.HttpMethods
import akka.http.scaladsl.model.Uri
import akka.stream.Materializer

class Rest()(
  implicit
  actorSystem: ActorSystem,
  materializer: Materializer,
) {

  val http = Http()

  val parsingResultToOption: HttpHeader.ParsingResult ⇒ Try[HttpHeader] = {
    case HttpHeader.ParsingResult.Ok(header, _) ⇒
      Success(header)
    case HttpHeader.ParsingResult.Error(err) ⇒
      Failure(new Exception(err.detail))
  }

  val parseHeaderRaw: ((String, String)) ⇒ HttpHeader.ParsingResult =
    (HttpHeader.parse(_: String, _: String)).tupled

  val parseHeader: ((String, String)) ⇒ Try[HttpHeader] =
    parseHeaderRaw.andThen(parsingResultToOption)

  val parseHeaders: Map[String, String] ⇒ Try[Seq[HttpHeader]] = _
    .map(parseHeader)
    .foldLeft(Success(Vector.empty): Try[Seq[HttpHeader]]) {
      case (Success(vec), Success(header)) ⇒
        Success(vec :+ header)
      case (Success(vec), Failure(err)) ⇒
        Failure(err)
      case (fail, _) ⇒
        fail
    }

  val makeRequest =
    (method: HttpMethod) ⇒
      (url: String, headers: Map[String, String], body: Seq[Byte]) ⇒
        parseHeaders(headers).map { headers ⇒
          HttpRequest(
            method = method,
            uri = Uri(url),
            headers = headers
          )
        }

  val post = makeRequest(HttpMethods.POST)
  val get = makeRequest(HttpMethods.GET)

  def send(req: HttpRequest): Future[HttpResponse] =
    http.singleRequest(req)
}

