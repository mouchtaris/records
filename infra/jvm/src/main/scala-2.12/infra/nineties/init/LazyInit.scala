package infra
package nineties
package init

import
  _root_.infra.{
    nineties ⇒ app,
  },
  scala.concurrent.{
    ExecutionContext,
  }

class LazyInit(implicit ec: ExecutionContext) extends InitPlace {
  lazy val config = Init[app.config.Config]()
  lazy val actorSystem = Init[ActorSystem]()
  lazy val streamSystem = Init[Materializer]()
  lazy val database = Init[DatabaseDef]()
}


