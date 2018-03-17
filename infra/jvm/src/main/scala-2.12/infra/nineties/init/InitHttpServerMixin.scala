package infra
package nineties
package init

trait InitHttpServerMixin {
  this: InitPlace ⇒

  final type InitHttpServer = Init[http.server.Binding]
  final implicit val InitHttpServer: InitHttpServer =
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
