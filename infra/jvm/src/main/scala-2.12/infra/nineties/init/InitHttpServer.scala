package infra
package nineties
package init

trait InitHttpServer {
  this: InitPlace ⇒

  implicit val InitHttpServer: Init[http.server.Binding] =
    () ⇒
      config flatMap { _config ⇒
        actorSystem flatMap { implicit _system ⇒
          streamSystem flatMap { implicit materialzier ⇒
            implicit val serverConfig = _config.httpServer
            details.akkahttp.Server().bind()
          }
        }
      }

}
