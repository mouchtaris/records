package mse2

import akka.http.scaladsl.model.Uri

object Client {
  object routes {

    val root = Uri("https://staging-api.musae.me:8443/api/v1")

  }
}

final case class Client(
  rest: Rest,
) {

  def ping() =
    ???

}
