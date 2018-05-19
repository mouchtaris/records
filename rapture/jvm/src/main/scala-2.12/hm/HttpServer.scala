package hm

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.model._
import akka.stream.Materializer
import akka.http.scaladsl.server._
import akka.http.scaladsl.server.Directives._

import scala.concurrent.Future
import scala.util.control.Exception.nonFatalCatch

class HttpServer(
  google: Google
)(
  implicit
  actorSystem: ActorSystem,
  materializer: Materializer
) {

  val USER_ID = "lolis"

  def html(body: String): String =
    s"""
       |<html>
       | <head>
       |   <title> Good Morning </title>
       | </head>
       | <body>
       |   $body
       | </body>
       |</html>
        """.stripMargin

  def completeHtml(body: String): Route = {
    val entity = HttpEntity(
      ContentTypes.`text/html(UTF-8)`,
      body
    )
    complete(entity)
  }

  val REDIRECT_URL = "http://localhost:9000/oauthcallback"
  def authUrl: String =
    google.authFlow.newAuthorizationUrl()
      .setState("The lols shall prevails")
      .setRedirectUri(REDIRECT_URL)
      .build()

  val http = Http()
  val route: Route =
    path("oauthcallback") {
      parameterMap { params ⇒
        val code = params.getOrElse("code", "NO")
        val token = google.authFlow.newTokenRequest(code).setRedirectUri(REDIRECT_URL)
        val tokenResponse = nonFatalCatch.withTry(token.execute()) match {
          case scala.util.Success(v) ⇒ v
          case scala.util.Failure(ex) ⇒
            println(s"shit: $ex")
            throw ex
        }
        google.authFlow.createAndStoreCredential(tokenResponse, USER_ID)
        val creds = google.authFlow.loadCredential(USER_ID)
        println(creds)
        val resp = HttpResponse(
          StatusCodes.Found,
          Vector(headers.Location("http://localhost:9000"))
        )
        complete(resp)
      }
    } ~
      path(PathMatchers.Remaining) { _ ⇒
        val creds = google.authFlow.loadCredential(USER_ID)
        val body =
          s"""
             |   <p> Good day, $creds </p>
             |   <p>
             |     Please
             |     <a href=$authUrl>
             |       Log In
             |     </a>
             |   </p>
            """.stripMargin
        completeHtml(body)
      }
  val binding: Future[Http.ServerBinding] = http.bindAndHandle(
    handler = route,
    interface = "0.0.0.0",
    port = 9000,
  )

}
